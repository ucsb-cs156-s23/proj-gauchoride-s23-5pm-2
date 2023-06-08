import { fireEvent, render, waitFor } from "@testing-library/react";
import { cartFixtures } from "fixtures/cartFixtures";
import CartTable from "main/components/Carts/CartTable";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import { currentUserFixtures } from "fixtures/currentUserFixtures";


const mockedNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockedNavigate
}));

describe("UserTable tests", () => {
  const queryClient = new QueryClient();


  test("renders without crashing for empty table with user not logged in", () => {
    const currentUser = null;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CartTable carts={[]} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );
  });
  test("renders without crashing for empty table for ordinary user", () => {
    const currentUser = currentUserFixtures.userOnly;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CartTable carts={[]} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );
  });

  test("renders without crashing for empty table for admin", () => {
    const currentUser = currentUserFixtures.adminUser;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CartTable carts={[]} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );
  });

  test("Has the expected colum headers and content for adminUser", () => {

    const currentUser = currentUserFixtures.adminUser;

    const { getByText, getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CartTable carts={cartFixtures.threeCarts} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );

    const expectedHeaders = ["id", "Name", "CapacityPeople", "CapacityWheelchair"];
    const expectedFields = ["id", "name", "capacityPeople", "capacityWheelchair"];
    const testId = "CartTable";

    expectedHeaders.forEach((headerText) => {
      const header = getByText(headerText);
      expect(header).toBeInTheDocument();
    });

    expectedFields.forEach((field) => {
      const header = getByTestId(`${testId}-cell-row-0-col-${field}`);
      expect(header).toBeInTheDocument();
    });

    expect(getByTestId(`${testId}-cell-row-0-col-id`)).toHaveTextContent("1");//1
    expect(getByTestId(`${testId}-cell-row-1-col-id`)).toHaveTextContent("2"); //2
    expect(getByTestId(`${testId}-cell-row-2-col-id`)).toHaveTextContent("3"); //3

    const editButton = getByTestId(`${testId}-cell-row-0-col-Edit-button`);
    expect(editButton).toBeInTheDocument();
    expect(editButton).toHaveClass("btn-primary");

    const deleteButton = getByTestId(`${testId}-cell-row-0-col-Delete-button`);
    expect(deleteButton).toBeInTheDocument();
    expect(deleteButton).toHaveClass("btn-danger");

  });

  test("Edit button navigates to the edit page for admin user", async () => {

    const currentUser = currentUserFixtures.adminUser;

    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CartTable carts={cartFixtures.threeCarts} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );

    await waitFor(() => { expect(getByTestId(`CartTable-cell-row-0-col-id`)).toHaveTextContent(1); })//1;

    const editButton = getByTestId(`CartTable-cell-row-0-col-Edit-button`);
    expect(editButton).toBeInTheDocument();

    fireEvent.click(editButton);

    await waitFor(() => expect(mockedNavigate).toHaveBeenCalledWith('/carts/edit/1'));//1

  });

  test("detail page button test", async () => {
    const currentUser = currentUserFixtures.adminUser;

    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CartTable carts={cartFixtures.threeCarts} currentUser={currentUser} actionVisible={true} />
        </MemoryRouter>
      </QueryClientProvider>
    );

    const detailButton = getByTestId("CartTable-cell-row-0-col-Detail-button");
    fireEvent.click(detailButton);
    await waitFor(() => expect(mockedNavigate).toHaveBeenCalledWith('/carts/detail/1')); //1
  });
  // Test that the table renders with the expected column headers and content for ordinary user
test("Has the expected column headers and content for Ordinary User", () => {

  const currentUser = currentUserFixtures.userOnly;

  const { getByText, getByTestId } = render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <CartTable carts={cartFixtures.threeCarts} currentUser={currentUser} />
      </MemoryRouter>
    </QueryClientProvider>
  );

  const expectedHeaders = ["id", "Name", "CapacityPeople", "CapacityWheelchair"];
  const expectedFields = ["id", "name", "capacityPeople", "capacityWheelchair"];
  const testId = "CartTable";

  expectedHeaders.forEach((headerText) => {
    const header = getByText(headerText);
    expect(header).toBeInTheDocument();
  });

  expectedFields.forEach((field) => {
    const header = getByTestId(`${testId}-cell-row-0-col-${field}`);
    expect(header).toBeInTheDocument();
  });

  expect(getByTestId(`${testId}-cell-row-0-col-id`)).toHaveTextContent("1");
  expect(getByTestId(`${testId}-cell-row-1-col-id`)).toHaveTextContent("2");
  expect(getByTestId(`${testId}-cell-row-2-col-id`)).toHaveTextContent("3");
});

// Test Delete button calls delete callback for ordinary user
test("Delete button calls delete callback for ordinary user", async () => {

  const currentUser = currentUserFixtures.userOnly;
  render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <CartTable carts={[]} currentUser={currentUser} />
      </MemoryRouter>
    </QueryClientProvider>
  );
});


// Test Delete button calls delete callback for admin user
test("Delete button calls delete callback for admin user", async () => {

  const currentUser = currentUserFixtures.adminUser;

  const { getByTestId } = render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <CartTable carts={cartFixtures.threeCarts} currentUser={currentUser} />
      </MemoryRouter>
    </QueryClientProvider>
  );
  const testId = "CartTable";
  await waitFor(() => { expect(getByTestId(`${testId}-cell-row-0-col-id`)).toHaveTextContent("1"); });

  const deleteButton = getByTestId(`${testId}-cell-row-0-col-Delete-button`);
  expect(deleteButton).toBeInTheDocument();
  expect(deleteButton).toHaveClass("btn-danger");

  fireEvent.click(deleteButton);

  await waitFor(() => expect(mockedNavigate).toHaveBeenCalledTimes(0));
  });
});

