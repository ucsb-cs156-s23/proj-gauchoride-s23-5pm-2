
import OurTable, { ButtonColumn } from "main/components/OurTable"
import { useBackendMutation } from "main/utils/useBackend";
//import UsersTable from './UsersTable';
//import {hasRole} from "main/utils/currentUser";


export default function UsersTable({ users}) {

    function cellToAxiosParamsToggleAdmin(cell) {
        return {
            url: "/api/admin/users/toggleAdmin",
            method: "POST",
            params: {
                id: cell.row.values.id
            }
        }
    }
    function cellToAxiosParamsToggleDriver(cell) {
        return {
            url: "/api/admin/users/toggleDriver",
            method: "POST",
            params: {
                id: cell.row.values.id
            }
        }
    }
    function cellToAxiosParamsToggleRider(cell) {
        return {
            url: "/api/admin/users/toggleRider",
            method: "POST",
            params: {
                id: cell.row.values.id
            }
        }
    }

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
    const toggleRiderMutation = useBackendMutation(
        cellToAxiosParamsToggleRider,
        {},
        ["/api/admin/users"]
    );
    // Stryker enable all 

    // Stryker disable next-line all : TODO try to make a good test for this
    const toggleAdminCallback = async (cell) => { toggleAdminMutation.mutate(cell); }
    const toggleDriverCallback = async (cell) => { toggleDriverMutation.mutate(cell); }
    const toggleRiderCallback = async (cell) => { toggleRiderMutation.mutate(cell); }

    const columns = [
        {
            Header: 'id',
            accessor: 'id', // accessor is the "key" in the data
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
            Header: 'Admin',
            id: 'admin',
            accessor: (row, _rowIndex) => String(row.admin) // hack needed for boolean values to show up
            //accessor: (row, _rowIndex) => <span data-testid={`admin-${row.id}`}>{String(row.admin)}</span> 
        },
        {
            Header: 'Driver',
            id: 'driver',
            accessor: (row, _rowIndex) => String(row.driver) // hack needed for boolean values to show up
            //accessor: (row, _rowIndex) => <span data-testid={`driver-${row.id}`}>{String(row.driver)}</span>
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
        ButtonColumn("toggle-admin", "primary", toggleAdminCallback, "UsersTable"),
        ButtonColumn("toggle-driver", "primary", toggleDriverCallback, "UsersTable"),
        ButtonColumn("toggle-rider", "primary", toggleRiderCallback, "UsersTable"),
    ]
    //const columnsToDisplay = showButtons ? buttonColumn : columns;
               
    return <OurTable
        data={users}
        columns={buttonColumn}
        testid={"UsersTable"}
    />;
};