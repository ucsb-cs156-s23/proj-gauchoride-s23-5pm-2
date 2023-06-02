import { BrowserRouter, Routes, Route } from "react-router-dom";
import HomePage from "main/pages/HomePage";
import ProfilePage from "main/pages/ProfilePage";
import AdminUsersPage from "main/pages/AdminUsersPage";
import DriverUsersPage from "main/pages/DriverUsersPage";
import ShiftTablePage from "main/pages/ShiftTablePage";



import { hasRole, useCurrentUser } from "main/utils/currentUser";

import "bootstrap/dist/css/bootstrap.css";


function App() {

  const { data: currentUser } = useCurrentUser();

  return (
    <BrowserRouter>
      <Routes>
        <Route exact path="/" element={<HomePage />} />
        <Route exact path="/profile" element={<ProfilePage />} />
        {
          hasRole(currentUser, "ROLE_ADMIN") && <Route exact path="/admin/users" element={<AdminUsersPage />} />
        }
        {
          hasRole(currentUser, "ROLE_DRIVER") && (
            <>
              <Route exact path="/driver/rides" element={<DriverUsersPage />} />
              <Route exact path="/driver/shift" element={<ShiftTablePage />} />
            </>
          )
        }
        {
          hasRole(currentUser, "ROLE_USER")
        }
      </Routes>
    </BrowserRouter>
  );
}

export default App;
