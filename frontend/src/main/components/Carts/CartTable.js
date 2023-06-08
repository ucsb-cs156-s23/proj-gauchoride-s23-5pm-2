import React from "react";
import OurTable, { ButtonColumn } from "main/components/OurTable";
import { useBackendMutation } from "main/utils/useBackend";
import { cellToAxiosParamsDelete, onDeleteSuccess } from "main/utils/cartUtils"
import { useNavigate } from "react-router-dom";
import { hasRole } from "main/utils/currentUser";

export default function CartTable({ carts, currentUser, actionVisible = true }) {

    const navigate = useNavigate();

    const editCallback = (cell) => {
        navigate(`/carts/edit/${cell.row.values.id}`)
    }

    const detailCallback = (cell) => {
        navigate(`/carts/detail/${cell.row.values.id}`)
    }

    // Stryker disable all : hard to test for query caching

    const deleteMutation = useBackendMutation(
        cellToAxiosParamsDelete,
        { onSuccess: onDeleteSuccess },
        ["/api/carts/all"]
    );
    // Stryker enable all 

    // Stryker disable next-line all : TODO try to make a good test for this
    const deleteCallback = async (cell) => { deleteMutation.mutate(cell); }

    const columns = [
        {
            Header: 'id',
            accessor: 'id', // accessor is the "key" in the data
        },

        {
            Header: 'Name',
            accessor: 'name',
        },
        {
            Header: 'CapacityPeople',
            accessor: 'capacityPeople',
        },
        {
            Header: 'CapacityWheelchair',
            accessor: 'capacityWheelchair',
        }
    ];

    if (hasRole(currentUser, "ROLE_ADMIN") && actionVisible) {
        columns.push(ButtonColumn("Detail", "primary", detailCallback, "CartTable"));
        columns.push(ButtonColumn("Edit", "primary", editCallback, "CartTable"));
        columns.push(ButtonColumn("Delete", "danger", deleteCallback, "CartTable"));
    }

    // Stryker disable next-line ArrayDeclaration: [columns] is a performance optimization
    const memoizedCarts = React.useMemo(() => carts, [carts]);
    const memoizedColumns = React.useMemo(() => columns, [columns]);

    return <OurTable
        data={memoizedCarts}
        columns={memoizedColumns}
        testid={"CartTable"}
    />;
};
