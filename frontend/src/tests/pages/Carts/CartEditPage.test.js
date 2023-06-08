import { fireEvent, render, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import CartEditPage from "main/pages/Carts/CartEditPage";
import { toast } from 'react-toastify';
import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

import mockConsole from "jest-mock-console";


jest.mock('react-toastify', () => {
    const originalModule = jest.requireActual('react-toastify');
    return {
        __esModule: true,
        ...originalModule,
        toast: {
            success: jest.fn(),
        }
    };
});

const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => {
    const originalModule = jest.requireActual('react-router-dom');
    return {
        __esModule: true,
        ...originalModule,
        useParams: () => ({
            id: 17
        }),
        Navigate: (x) => { mockNavigate(x); return null; }
    };
});

describe("CartEditPage tests", () => {

    describe("when the backend doesn't return a todo", () => {

        const axiosMock = new AxiosMockAdapter(axios);

        beforeEach(() => {
            axiosMock.reset();
            axiosMock.resetHistory();
            axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
            axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
            axiosMock.onGet("/api/carts", { params: { id: 17 } }).timeout();
        });

        const queryClient = new QueryClient();
        test("renders header but table is not present", async () => {

            const restoreConsole = mockConsole();

            const { queryByTestId, findByText } = render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <CartEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );
            await findByText("Edit Cart");
            expect(queryByTestId("CartForm-name")).not.toBeInTheDocument();
            restoreConsole();
        });
    });

    describe("tests where backend is working normally", () => {

        const axiosMock = new AxiosMockAdapter(axios);

        beforeEach(() => {
            axiosMock.reset();
            axiosMock.resetHistory();
            axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
            axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
            axiosMock.onGet("/api/carts", { params: { id: 17 } }).reply(200, {
                id: 17,
                name: "some test name 17",
                capacityPeople: "some test capacitypeople 17",
                capacityWheelchair: "some test capacitywheelchair 17"
            });
            axiosMock.onPut('/api/carts').reply(200, {
                id: "17",
                name: "some test name 19",

                capacityWheelchair: "some test capacitywheelchair 19"
            });
        });

        const queryClient = new QueryClient();
        test("renders without crashing", () => {
            render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <CartEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );
        });

        test("Is populated with the data provided", async () => {

            const { getByTestId, findByTestId } = render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <CartEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );

            await findByTestId('CartForm-name');

            const idField = getByTestId('CartForm-id');
            const nameField = getByTestId('CartForm-name');
            const capacityPeopleField = getByTestId('CartForm-capacityPeople');
            const capacityWheelchairField = getByTestId('CartForm-capacityWheelchair');

            expect(idField).toHaveValue("17");
            expect(nameField).toHaveValue("some test name 17");
            expect(capacityPeopleField).toHaveValue("some test capacitypeople 17");
            expect(capacityWheelchairField).toHaveValue("some test capacitywheelchair 17");
        });

        test("Changes when you click Update", async () => {



            const { getByTestId, findByTestId } = render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <CartEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );

            const idField = getByTestId('CartForm-id');
            const nameField = getByTestId('CartForm-name');
            const capacityPeopleField = getByTestId('CartForm-capacityPeople');
            const capacityWheelchairField = getByTestId('CartForm-capacityWheelchair');
            const submitButton = getByTestId('CartForm-submit');

            expect(idField).toHaveValue("17");
            expect(nameField).toHaveValue("some test name 17");
            expect(capacityPeopleField).toHaveValue("some test capacitypeople 17");
            expect(capacityWheelchairField).toHaveValue("some test capacitywheelchair 17");
            expect(submitButton).toBeInTheDocument();

            fireEvent.change(nameField, { target: { value: 'some test name 19' } })
            fireEvent.change(capacityPeopleField, { target: { value: "some test capacitypeople 19" } })
            fireEvent.change(capacityWheelchairField, { target: { value: "some test capacitywheelchair 19" } })
            fireEvent.click(submitButton);

            await waitFor(() => expect(toast.success).toBeCalled);
            expect(toast.success).toBeCalledWith("Cart Updated - id: 17 name: some test name 19");
            expect(mockNavigate).toBeCalledWith({ "to": "/carts/list" });

            expect(axiosMock.history.put.length).toBe(1); // times called
            expect(axiosMock.history.put[0].params).toEqual({ id: 17 });
            expect(axiosMock.history.put[0].data).toBe(JSON.stringify({
                name: "some test name 19",
                capacityPeople: "some test capacitypeople 19",
                capacityWheelchair: "some test capacitywheelchair 19"
            })); // posted object

        });


    });
});

