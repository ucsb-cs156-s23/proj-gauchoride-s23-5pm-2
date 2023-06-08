import React from 'react'
import { useBackend } from 'main/utils/useBackend';

import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import CartTable from 'main/components/Carts/CartTable';
import { useCurrentUser } from 'main/utils/currentUser'

export default function CartIndexPage() {

  const currentUser = useCurrentUser();

  const { data: carts, error: _error, status: _status } =
    useBackend(
      // Stryker disable next-line all : don't test internal caching of React Query
      ["/api/carts/all"],
      { method: "GET", url: "/api/carts/all" },
      []
    );

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Carts</h1>
        <CartTable carts={carts} currentUser={currentUser} />
      </div>
    </BasicLayout>
  )
}