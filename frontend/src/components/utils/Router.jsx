import { BrowserRouter, Route, Routes } from "react-router-dom";
import { SamplesPage } from '../../react-envelope/components/pages/SamplesPage/SamplesPage';
import { PrivateRoute } from "../../react-envelope/utils/PrivateRoute";
import { DevExpPage } from "../../react-envelope/components/pages/development/DevExpPage/DevExpPage";
import { UserSettingsPage } from "../pages/user/UserSettingsPage/UserSettingsPage";
import { useEffect } from "react";
import { useNavigation } from "../../react-envelope/hooks/useNavigation";
import { Code, Crown, Dollar, Electricity, Home, Package, Pizza } from "../../react-envelope/components/dummies/Icons";
import { DocsPage } from "../../react-envelope/components/pages/development/DocsPage/DocsPage";
import { ScrollRestoration } from "../../react-envelope/utils/ScrollRestoration";
import { AuthPage } from "../pages/user/AuthPage/AuthPage";
import { HomePage } from "../pages/general/HomePage/HomePage";
import { TariffPage } from "../pages/general/TariffPage/TariffPage";
import { ModeratorsManagementPage } from "../pages/admin/ModeratorsManagementPage/ModeratorsManagementPage";
import { PowerSupplyDataPage } from "../pages/admin/PowerSupplyDataPage/PowerSupplyDataPage";

export const Router = () => {
    const { routes, add } = useNavigation();

    useEffect(() => {
        add({
            name: 'Главная',
            to: '/',
            props: {
                icon: <Home />
            }
        }, {
            name: 'Калькулятор',
            to: '/calc',
            props: {
                icon: <Dollar />
            }
        }, {
            name: 'Модерация',
            to: '/mods',
            requireAuth: true,
            roles: 'admin',
            props: {
                icon: <Crown />
            }
        }, {
            name: 'Сетевая нагрузка',
            to: '/supply',
            requireAuth: true,
            roles: 'admin',
            props: {
                icon: <Electricity />
            }
        }, {
            name: 'ENVELOPE 2.0',
            to: '/_lab/docs',
            permissions: 'dev',
            props: {
                icon: <Package />
            }
        }, {
            name: 'ENVELOPE',
            to: '/_lab/old',
            permissions: 'dev',
            props: {
                icon: <Pizza />
            }
        }, {
            name: 'Экспериментальная',
            to: '/_lab/exp',
            permissions: 'dev',
            props: {
                icon: <Code />,
                className: 'debug'
            }
        });
    }, [])

    return (
        <BrowserRouter>
            <ScrollRestoration />
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/calc" element={<TariffPage />} />

                <Route element={<PrivateRoute/>}>
                    <Route path="/mods" element={<ModeratorsManagementPage/>}/>
                    <Route path="/supply" element={<PowerSupplyDataPage/>}/>
                </Route>

                <Route path="/_lab" element={<PrivateRoute roles={'dev'} />}>
                    <Route path="docs" element={<DocsPage />} />
                    <Route path="old" element={<SamplesPage />} />
                    <Route path="exp" element={<DevExpPage />} />
                </Route>

                <Route path="/user">
                    <Route path="auth" element={<AuthPage />} />

                    <Route element={<PrivateRoute />}>
                        <Route path="settings" element={<UserSettingsPage />} />
                    </Route>
                </Route>

                <Route path="/profile/:tag" element={'User profile'} />
            </Routes>
        </BrowserRouter>
    );
};