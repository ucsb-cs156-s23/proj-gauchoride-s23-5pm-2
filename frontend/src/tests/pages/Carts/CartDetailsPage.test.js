import {render, waitFor } from "@testing-library/react";
import CartDetailsPage from "main/pages/Carts/CartDetailsPage";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import { apiCurrentUserFixtures }  from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import axios from "axios";
import { toast } from 'react-toastify'
import AxiosMockAdapter from "axios-mock-adapter";
import mockConsole from "jest-mock-console";
import * as backendModule from "main/utils/useBackend";

const CARTS_TABLE_TEST_ID = "CartTable";

const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useParams: () => ({
        id: 3
    }),
    useNavigate: (x) => { mockNavigate(x); return null; },
}));


jest.mock('react-toastify', () => {
    const originalModule = jest.requireActual('react-toastify');
    return {
        __esModule: true,
        ...originalModule,
        toast: {
            error: jest.fn(),
        }
    };
});




describe("CartDetailsPage tests", () => {
    const axiosMock =new AxiosMockAdapter(axios);

    const setupUserOnly = () => {
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
    };

    const setupAdminUser = () => {
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.adminUser);
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
    };

    test("loads the correct fields, and no buttons, not admin", async ()=>{
        setupUserOnly();
        const queryClient = new QueryClient();
        const cart =  {
            "id": 3,
            "name": "Cart3",
            "capacityPeople": 2,
            "capacityWheelchair": 2
        }

        axiosMock.onGet("/api/carts?id=3").reply(202, cart);

        const { getByTestId, queryByTestId } = render(
            <QueryClientProvider client={queryClient}>
            <MemoryRouter>
                <CartDetailsPage />
            </MemoryRouter>
            </QueryClientProvider>
        )

        await waitFor(() => { expect(getByTestId(`${CARTS_TABLE_TEST_ID}-cell-row-0-col-id`)).toHaveTextContent("3"); });
        expect(getByTestId(`${CARTS_TABLE_TEST_ID}-cell-row-0-col-name`)).toHaveTextContent("Cart3");
        expect(getByTestId(`${CARTS_TABLE_TEST_ID}-cell-row-0-col-capacityPeople`)).toHaveTextContent(2);
        expect(getByTestId(`${CARTS_TABLE_TEST_ID}-cell-row-0-col-capacityWheelchair`)).toHaveTextContent(2);
        expect(queryByTestId(`${CARTS_TABLE_TEST_ID}-cell-row-0-col-Detail-button`)).not.toBeInTheDocument();
        expect(queryByTestId(`${CARTS_TABLE_TEST_ID}-cell-row-0-col-Edit-button`)).not.toBeInTheDocument();
        expect(queryByTestId(`${CARTS_TABLE_TEST_ID}-cell-row-0-col-Delete-button`)).not.toBeInTheDocument();
    });

    test("loads the correct fields, and no buttons, admin user", async ()=>{
        setupAdminUser();
        const queryClient = new QueryClient();
        const cart =  {
            "id": 3,
            "name": "Cart3",
            "capacityPeople": 2,
            "capacityWheelchair": 2
        }

        axiosMock.onGet("/api/carts?id=3").reply(202, cart);

        const { getByTestId, queryByTestId } = render(
            <QueryClientProvider client={queryClient}>
            <MemoryRouter>
                <CartDetailsPage />
            </MemoryRouter>
            </QueryClientProvider>
        )

        await waitFor(() => { expect(getByTestId(`${CARTS_TABLE_TEST_ID}-cell-row-0-col-id`)).toHaveTextContent("3"); });
        expect(getByTestId(`${CARTS_TABLE_TEST_ID}-cell-row-0-col-name`)).toHaveTextContent("Cart3");
        expect(getByTestId(`${CARTS_TABLE_TEST_ID}-cell-row-0-col-capacityPeople`)).toHaveTextContent(2);
        expect(getByTestId(`${CARTS_TABLE_TEST_ID}-cell-row-0-col-capacityWheelchair`)).toHaveTextContent(2);
        expect(queryByTestId(`${CARTS_TABLE_TEST_ID}-cell-row-0-col-Detail-button`)).not.toBeInTheDocument();
        expect(queryByTestId(`${CARTS_TABLE_TEST_ID}-cell-row-0-col-Edit-button`)).not.toBeInTheDocument();
        expect(queryByTestId(`${CARTS_TABLE_TEST_ID}-cell-row-0-col-Delete-button`)).not.toBeInTheDocument();
    });

    test("toast error notification and console error message when id doesn't exit in database", async ()=> {
        setupUserOnly();
        const queryClient = new QueryClient();
        const restoreConsole = mockConsole();

        render(
            <QueryClientProvider client={queryClient}>
            <MemoryRouter>
                <CartDetailsPage />
            </MemoryRouter>
            </QueryClientProvider>
        )

        await waitFor(() => { expect(axiosMock.history.get.length).toBeGreaterThanOrEqual(1); });
        
        await waitFor(() => {expect(toast.error)})
        
        restoreConsole();

        expect(toast.error).toBeCalled();
        expect(toast.error).toHaveBeenCalledWith("Error communicating with backend via GET on /api/carts?id=3");
    });

    test('useBackend params are correct', async () => {
        setupAdminUser();
        const queryClient = new QueryClient();
        const useBackendSpyFunc = jest.spyOn(backendModule, 'useBackend');

        const cart =  {
            "id": 3,
            "name": "Cart3",
            "capacityPeople": 2,
            "capacityWheelchair": 2
        }

        axiosMock.onGet("/api/carts?id=3").reply(202, cart);

        render(
            <QueryClientProvider client={queryClient}>
            <MemoryRouter>
                <CartDetailsPage />
            </MemoryRouter>
            </QueryClientProvider>
        )

        await waitFor(() => { expect(axiosMock.history.get.length).toBeGreaterThanOrEqual(1); });
        expect(useBackendSpyFunc).toBeCalled();
        expect(useBackendSpyFunc).toBeCalledWith( ["/api/carts/detail/3"], { method: "GET", url: `/api/carts?id=3`}, []);
    });


});

