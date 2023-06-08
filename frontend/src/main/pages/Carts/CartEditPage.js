import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import { useParams } from "react-router-dom";
import CartForm from "main/components/Carts/CartForm";
import { useBackendMutation, useBackend } from "main/utils/useBackend";
import { toast } from 'react-toastify';
import { Navigate } from "react-router-dom";

const CartEditPage = () => {
    let { id } = useParams();

    const { data: cart, error, status } =
    useBackend(
      // Stryker disable next-line all : don't test internal caching of React Query
      [`/api/carts?id=${id}`],
      {  // Stryker disable next-line all : GET is the default, so changing this to "" doesn't introduce a bug
        method: "GET",
        url: `/api/carts`,
        params: {
          id
        }
      }
    );

    const objectToAxiosPutParams = (cart) => ({
        url: "/api/carts",
        method: "PUT",
        params: {
            id: cart.id,
        },
        data: {
            name: cart.name,
            capacityPeople: cart.capacityPeople,
            capacityWheelchair: cart.capacityWheelchair
        }
    });

    const onSuccess = (cart) => {
        toast.success(`Cart Updated - id: ${cart.id} name: ${cart.name}`);
    }

    const mutation = useBackendMutation(
        objectToAxiosPutParams,
        { onSuccess },
        // Stryker disable next-line all : hard to set up test for caching
        [`/api/carts?id=${id}`]
    )
    const {isSuccess} = mutation

    if (isSuccess) {
        return <Navigate to="/carts/list"/>
    }

    const onSubmit = async (formData) => {
        mutation.mutate(formData);
    }

    return (
        <BasicLayout>
            <div className="pt-2 pb-3">
                <h1>Edit Cart</h1>
                {cart && 
                <CartForm submitAction={onSubmit} buttonLabel="Update" initialContents={cart} /> 
                }
            </div>
        </BasicLayout>
    )
}

export default CartEditPage;