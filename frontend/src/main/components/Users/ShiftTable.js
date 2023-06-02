import OurTable from "main/components/OurTable"

export default function ShiftTable({ shift}) {
    // Stryker enable all 

    // Stryker disable next-line all : TODO try to make a good test for this
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
            Header: 'Day',
            accessor: 'day',
        },
        {
            Header: 'Start Time',
            accessor: 'StartTime',
        },
        {
            Header: 'End Time',
            accessor: 'EndTime',
        },
    ];
               
    return <OurTable
        data={shift}
        columns = {columns}
        testid={"ShiftTable"}
    />;
};