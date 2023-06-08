import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import { useParams } from "react-router-dom";
import CartTable from 'main/components/Carts/CartTable';
import { useBackend } from "main/utils/useBackend";
import { useCurrentUser } from 'main/utils/currentUser';

export default function CartDetailsPage() {
  let { id } = useParams();

  const currentUser = useCurrentUser();

  const { data: carts, error: _error, status: _status } =
  useBackend(
    // Stryker disable next-line all : don't test internal caching of React Query
    [`/api/carts/detail/${id}`],
    { method: "GET", url: `/api/carts?id=${id}` },
    []
  );

  
  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Cart Details</h1>
        <CartTable carts={[carts]} currentUser={currentUser} actionVisible={false} />
      </div>
    </BasicLayout>
  )
}
