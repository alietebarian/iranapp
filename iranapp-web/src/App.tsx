import { lazy, Suspense, useEffect, useState } from 'react';
import { Navigate, Outlet, Route, Routes, useLocation, useNavigate } from 'react-router';
import { REQUIRE_LOGIN } from './config';
import { GlobalDialogs } from './components/dialogs';
import { ConfirmHost, Loading, Toaster } from './components/ui';
import { AUTH_EXPIRED_EVENT } from './lib/http';
import { syncPushToken } from './lib/push';
import { useSession } from './store/session';
import { toast } from './store/ui';
import Splash from './pages/Splash';
import MainLayout from './pages/main/MainLayout';
import KasbokarTab from './pages/main/KasbokarTab';
import ClassifiedsTab from './pages/main/ClassifiedsTab';
import NewsTab from './pages/main/NewsTab';
import FavoritesTab from './pages/main/FavoritesTab';
import MenuTab from './pages/main/MenuTab';
import { ConfirmPage, IntroPage, LoginPage, SignUpPage, WelcomePage } from './pages/auth';
import AdListPage from './pages/AdListPage';
import AdDetailPage from './pages/AdDetailPage';
import { EstateListPage, JobListPage, VehicleListPage } from './pages/classifiedLists';
import { EstateDetailPage, JobDetailPage, VehicleDetailPage } from './pages/classifiedDetails';
import NewsDetailPage from './pages/NewsDetailPage';

// Screens most visitors never open are split out of the first download.
const ClassifiedSearchPage = lazy(() => import('./pages/ClassifiedSearchPage'));
const DashboardPage = lazy(() => import('./pages/DashboardPage'));
const AccountPages = {
  Edit: lazy(() => import('./pages/account').then((m) => ({ default: m.EditAccountPage }))),
  Password: lazy(() => import('./pages/account').then((m) => ({ default: m.ChangePasswordPage }))),
};
const BusinessFormPage = lazy(() => import('./pages/forms/BusinessFormPage'));
const VehicleFormPage = lazy(() => import('./pages/forms/VehicleFormPage'));
const EstateFormPage = lazy(() => import('./pages/forms/EstateFormPage'));
const JobFormPage = lazy(() => import('./pages/forms/JobFormPage'));
const AdStatsPage = lazy(() => import('./pages/AdStatsPage'));
const AroundMePage = lazy(() => import('./pages/AroundMePage'));
const EContractPage = lazy(() => import('./pages/EContractPage'));
const MembershipCardPage = lazy(() => import('./pages/MembershipCardPage'));
const AboutPage = lazy(() => import('./pages/AboutPage'));
const HelpPage = lazy(() => import('./pages/HelpPage'));

/**
 * The Android app does not let guests in: after the splash a new user sees the intro, then
 * «ورود / ثبت نام». The same gate guards every page here (turn it off with VITE_REQUIRE_LOGIN=false).
 */
function Gate() {
  const location = useLocation();
  const user = useSession((state) => state.user);
  const token = useSession((state) => state.token);
  const introSeen = useSession((state) => state.introSeen);
  const from = location.pathname + location.search;

  if (!introSeen) return <Navigate to="/intro" replace state={{ from }} />;
  if (user && (!user.verified || !token)) return <Navigate to="/confirm" replace state={{ from }} />;
  if (!user && REQUIRE_LOGIN) return <Navigate to="/welcome" replace state={{ from }} />;
  return <Outlet />;
}

export default function App() {
  const navigate = useNavigate();
  const [splash, setSplash] = useState(() => sessionStorage.getItem('iranapp:splash') !== 'done');

  useEffect(() => {
    const onExpired = () => {
      toast.failure('لطفا دوباره وارد حساب خود شوید');
      navigate('/login');
    };
    window.addEventListener(AUTH_EXPIRED_EVENT, onExpired);
    return () => window.removeEventListener(AUTH_EXPIRED_EVENT, onExpired);
  }, [navigate]);

  useEffect(() => {
    syncPushToken();
  }, []);

  if (splash) {
    return (
      <Splash
        onDone={() => {
          sessionStorage.setItem('iranapp:splash', 'done');
          setSplash(false);
        }}
      />
    );
  }

  return (
    <>
      <Suspense fallback={<Loading className="h-full" />}>
        <Routes>
          <Route path="/intro" element={<IntroPage />} />
          <Route path="/welcome" element={<WelcomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignUpPage />} />
          <Route path="/confirm" element={<ConfirmPage />} />

          <Route element={<Gate />}>
            <Route element={<MainLayout />}>
              <Route index element={<KasbokarTab />} />
              <Route path="classifieds" element={<ClassifiedsTab />} />
              <Route path="news" element={<NewsTab />} />
              <Route path="favorites" element={<FavoritesTab />} />
              <Route path="menu" element={<MenuTab />} />
            </Route>

            <Route path="ads/:subCategoryId" element={<AdListPage />} />
            <Route path="ad/:id" element={<AdDetailPage />} />
            <Route path="ad/:id/stats" element={<AdStatsPage />} />
            <Route path="news/:id" element={<NewsDetailPage />} />

            <Route path="vehicles" element={<VehicleListPage />} />
            <Route path="vehicle/:id" element={<VehicleDetailPage />} />
            <Route path="estates" element={<EstateListPage />} />
            <Route path="estate/:id" element={<EstateDetailPage />} />
            <Route path="jobs" element={<JobListPage />} />
            <Route path="job/:id" element={<JobDetailPage />} />
            <Route path="search/:kind" element={<ClassifiedSearchPage />} />

            <Route path="dashboard" element={<DashboardPage />} />
            <Route path="account/edit" element={<AccountPages.Edit />} />
            <Route path="account/password" element={<AccountPages.Password />} />

            <Route path="new/business" element={<BusinessFormPage />} />
            <Route path="edit/business/:id" element={<BusinessFormPage />} />
            <Route path="new/vehicle" element={<VehicleFormPage />} />
            <Route path="edit/vehicle/:id" element={<VehicleFormPage />} />
            <Route path="new/estate" element={<EstateFormPage />} />
            <Route path="edit/estate/:id" element={<EstateFormPage />} />
            <Route path="new/job" element={<JobFormPage />} />
            <Route path="edit/job/:id" element={<JobFormPage />} />

            <Route path="around" element={<AroundMePage />} />
            <Route path="e-contract" element={<EContractPage />} />
            <Route path="membership-card" element={<MembershipCardPage />} />
            <Route path="about" element={<AboutPage />} />
            <Route path="help" element={<HelpPage />} />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Suspense>
      <GlobalDialogs />
      <ConfirmHost />
      <Toaster />
    </>
  );
}
