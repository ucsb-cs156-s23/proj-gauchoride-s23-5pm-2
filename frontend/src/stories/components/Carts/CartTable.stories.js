import React from 'react';

import CartTable from 'main/components/Carts/CartTable';
import { cartFixtures } from 'fixtures/cartFixtures';

export default {
    title: 'components/Carts/CartTable',
    component: CartTable
};

const Template = (args) => {
    return (
        <CartTable {...args} />
    )
}

export const Empty = Template.bind({});

Empty.args = {
    carts: []
};

export const ThreeCartsNoAdmin = Template.bind({})

ThreeCartsNoAdmin.args = {
    carts: cartFixtures.threeCarts,
    currentUser: {}
}

export const ThreeCartsButtonColumn = Template.bind({});

ThreeCartsButtonColumn.args = {
    carts: cartFixtures.threeCarts,
    actionVisible: true,
    currentUser: {
        data: {
        root: {
            rolesList: ['ROLE_ADMIN']
        }
    } }
}

export const ThreeCartsButtonColumnInVisible = Template.bind({});

ThreeCartsButtonColumnInVisible.args = {
    carts: cartFixtures.threeCarts,
    actionVisible: false,
    currentUser: {
        data: {
        root: {
            rolesList: ['ROLE_ADMIN']
        }
    } }
}