import React from "react";
import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import ShiftTable from "main/components/Users/ShiftTable"

import { useBackend } from "main/utils/useBackend";
const ShiftTablePage = () => {

    const { data: shift, error: _error, status: _status } =
        useBackend(
            // Stryker disable next-line all : don't test internal caching of React Query
            ["/api/driver/shift"],
            // Stryker disable next-line StringLiteral,ObjectLiteral : since "GET" is default, "" is an equivalent mutation
            { method: "GET", url: "/api/driver/shift" },
            []
        );

    return (
        <BasicLayout>
            <h2>Shifts</h2>
            <ShiftTable shift={shift} />
        </BasicLayout>
    );
};

export default ShiftTablePage;