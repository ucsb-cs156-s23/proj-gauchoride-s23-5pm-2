import React from 'react';

import CartForm  from 'main/components/Carts/CartForm';
import { cartFixtures } from 'fixtures/cartFixtures';

export default {
    title: 'components/Carts/CartForm',
    component: CartForm
};


const Template = (args) => {
    return (
        <CartForm {...args} />
    )
};

export const Default = Template.bind({});

Default.args = {
    submitText: "Create",
    submitAction: () => { console.log("Submit was clicked"); }
};

export const Show = Template.bind({});

Show.args = {
    cart: cartFixtures.oneCart,
    submitText: "",
    submitAction: () => { }
};
