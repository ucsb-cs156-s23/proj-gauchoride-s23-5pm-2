import { render, waitFor, fireEvent } from "@testing-library/react";
import CartCreatePage from "main/pages/Carts/CartCreatePage";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";

import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";


const mockToast = jest.fn();
jest.mock('react-toastify', () => {
    const originalModule = jest.requireActual('react-toastify');
    return {
        __esModule: true,
        ...originalModule,
        toast: (x) => mockToast(x)
    };
});

const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => {
    const originalModule = jest.requireActual('react-router-dom');
    return {
        __esModule: true,
        ...originalModule,
        Navigate: (x) => { mockNavigate(x); return null; }
    };
});

describe("CartCreatePage tests", () => {

    const axiosMock =new AxiosMockAdapter(axios);

    beforeEach(() => {
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
    });

    test("renders without crashing", () => {
        const queryClient = new QueryClient();
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <CartCreatePage />
                </MemoryRouter>
            </QueryClientProvider>
        );
    });

    test("when you fill in the form and hit submit, it makes a request to the backend", async () => {

        const queryClient = new QueryClient();
        const cart = {
            id: 17,
            name: "Cart1",
            capacityPeople: parseInt(2),
            capacityWheelchair: parseInt(1)
        };

        axiosMock.onPost("/api/carts/post").reply( 202, cart );

        const { getByTestId } = render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <CartCreatePage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        await waitFor(() => {
            expect(getByTestId("CartForm-name")).toBeInTheDocument();
        });

        const nameField = getByTestId("CartForm-name");
        const capacityPeopleField = getByTestId("CartForm-capacityPeople");
        const capacityWheelchairField = getByTestId("CartForm-capacityWheelchair");
        const submitButton = getByTestId("CartForm-submit");

        fireEvent.change(nameField, { target: { value: 'Cart2' } });
        fireEvent.change(capacityPeopleField, { target: { value: parseInt(3) } });
        fireEvent.change(capacityWheelchairField, { target: { value: parseInt(2) } });

        expect(submitButton).toBeInTheDocument();

        fireEvent.click(submitButton);

        await waitFor(() => expect(axiosMock.history.post.length).toBe(1));

        expect(axiosMock.history.post[0].params).toEqual(
            {
            "capacityWheelchair": "2",
            "capacityPeople": "3",
            "name": "Cart2"
        });

        expect(mockToast).toBeCalledWith("New cart Created - id: 17 name: Cart1");
        expect(mockNavigate).toBeCalledWith({ "to": "/carts/list" });
    });

});


