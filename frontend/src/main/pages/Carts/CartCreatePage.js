import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import CartForm from "main/components/Carts/CartForm";
import { Navigate } from 'react-router-dom'
import { useBackendMutation } from "main/utils/useBackend";
import { toast } from "react-toastify";

export default function CartCreatePage() {

  const objectToAxiosParams = (cart) => ({
    url: "/api/carts/post",
    method: "POST",
    params: {
      name: cart.name,
      capacityPeople: cart.capacityPeople,
      capacityWheelchair: cart.capacityWheelchair
    }
  });

  const onSuccess = (cart) => {
    toast(`New cart Created - id: ${cart.id} name: ${cart.name}`);
  }

  const mutation = useBackendMutation(
    objectToAxiosParams,
     { onSuccess }, 
     // Stryker disable next-line all : hard to set up test for caching
     ["/api/carts/all"]
     );

  const { isSuccess } = mutation

  const onSubmit = async (data) => {
    mutation.mutate(data);
  }

  if (isSuccess) {
    return <Navigate to="/carts/list" />
  }

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Create New Cart</h1>

        <CartForm submitAction={onSubmit} />

      </div>
    </BasicLayout>
  )
}