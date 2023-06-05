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
            Header: 'Rider',
            accessor: 'rider',
        },
        {
            Header: 'Driver',
            accessor: 'driver',
        },
        {
            Header: 'Course',
            accessor: 'course',
        },
        {
            Header: 'Building',
            accessor: 'building',
        },
        {
            Header: 'Room',
            accessor: 'room',
        },
        {
            Header: 'Pick Up',
            accessor: 'pickUp',
        },
        {
            Header: 'Phone Number',
            accessor: 'phoneNumber',
        },
    ];
               
    return <OurTable
        data={rides}
        columns = {columns}
        testid={"RidesTable"}
    />;
};