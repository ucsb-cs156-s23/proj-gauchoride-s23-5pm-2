export default function RidesTable({ rides}) {

    // function cellToAxiosParamsToggleAdmin(cell) {
    //     return {
    //         url: "/api/admin/users/toggleAdmin",
    //         method: "POST",
    //         params: {
    //             id: cell.row.values.id
    //         }
    //     }
    // }
    // function cellToAxiosParamsToggleDriver(cell) {
    //     return {
    //         url: "/api/admin/users/toggleDriver",
    //         method: "POST",
    //         params: {
    //             id: cell.row.values.id
    //         }
    //     }
    // }

    // Stryker disable all : hard to test for query caching
    const toggleAdminMutation = useBackendMutation(
        cellToAxiosParamsToggleAdmin,
        {},
        ["/api/admin/users"]
    );
    const toggleDriverMutation = useBackendMutation(
        cellToAxiosParamsToggleDriver,
        {},
        ["/api/admin/users"]
    );
    // Stryker enable all 

    // Stryker disable next-line all : TODO try to make a good test for this
    const toggleAdminCallback = async (cell) => { toggleAdminMutation.mutate(cell); }
    const toggleDriverCallback = async (cell) => { toggleDriverMutation.mutate(cell); }

    const columns = [
        {
            Header: 'id',
            accessor: 'id', // accessor is the "key" in the data
        },
        {
            Location: 'Pickup Location',
            accessor: 'givenLocation',
        },
        {
            Header: 'First Name',
            accessor: 'givenName',
        },
        {
            Header: 'Last Name',
            accessor: 'familyName',
        },
        {
            Header: 'Email',
            accessor: 'email',
        },
        {
            Header: 'Driver',
            id: 'driver',
            accessor: (row, _rowIndex) => String(row.driver) // hack needed for boolean values to show up
            //accessor: (row, _rowIndex) => <span data-testid={`admin-${row.id}`}>{String(row.admin)}</span> 
        },
        {
            Header: 'Rider',
            id: 'rider',
            accessor: (row, _rowIndex) => String(row.rider) // hack needed for boolean values to show up
            //accessor: (row, _rowIndex) => <span data-testid={`driver-${row.id}`}>{String(row.driver)}</span>
        }
    ];
    const buttonColumn = [
        ...columns,
    ]
               
    return <OurTable
        data={rides}
        columns = {buttonColumn}
        testid={"RidesTable"}
    />;
};