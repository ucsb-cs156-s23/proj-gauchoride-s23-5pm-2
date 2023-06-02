import OurTable from "main/components/OurTable"

export default function RidesTable({ rides}) {
    // Stryker enable all 

    // Stryker disable next-line all : TODO try to make a good test for this
    const columns = [
        {
            Header: 'id',
            accessor: 'id', // accessor is the "key" in the data
        },
        {
            Header: 'Pickup Location',
            accessor: 'givenStartLocation',
        },
        {
            Header: 'Drop-off Location',
            accessor: 'givenEndLocation',
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
            Header: 'Phone number',
            accessor: 'phone',
        },
    ];
               
    return <OurTable
        data={rides}
        columns = {columns}
        testid={"RidesTable"}
    />;
};