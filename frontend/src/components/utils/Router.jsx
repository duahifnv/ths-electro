import { BrowserRouter, Route, Routes } from "react-router-dom";
import { SamplesPage } from '../../react-envelope/components/pages/SamplesPage/SamplesPage';
import { PrivateRoute } from "../../react-envelope/utils/PrivateRoute";
import { DevExpPage } from "../../react-envelope/components/pages/development/DevExpPage/DevExpPage";
import { UserSettingsPage } from "../pages/user/UserSettingsPage/UserSettingsPage";
import { useEffect } from "react";
import { useNavigation } from "../../react-envelope/hooks/useNavigation";
import { Code, Crown, Database, Dollar, Electricity, Home, Package, Pizza } from "../../react-envelope/components/dummies/Icons";
import { DocsPage } from "../../react-envelope/components/pages/development/DocsPage/DocsPage";
import { ScrollRestoration } from "../../react-envelope/utils/ScrollRestoration";
import { AuthPage } from "../pages/user/AuthPage/AuthPage";
import { HomePage } from "../pages/general/HomePage/HomePage";
import { TariffPage } from "../pages/general/TariffPage/TariffPage";
import { ModeratorsManagementPage } from "../pages/admin/ModeratorsManagementPage/ModeratorsManagementPage";
import { PowerSupplyDataPage } from "../pages/admin/database/PowerSupplyDataPage/PowerSupplyDataPage";
import ChatWidget from "../ui/ChatWidget/ChatWidget";
import { HolidaysDatabasePage } from "../pages/admin/database/HolidaysDatabasePage";
import { DayZonesDatabasePage } from "../pages/admin/database/DayZonesDatabasePage";
import { AccountingHoursDatabasePage } from "../pages/admin/database/AccountingHoursDatabasePage";

export const Router = () => {
    const { routes, add } = useNavigation();

    useEffect(() => {
        add(
        // {
        //     name: 'Главная',
        //     to: '/',
        //     props: {
        //         icon: <Home />
        //     }
        // }, 
        {
            name: 'Калькулятор',
            to: '/',
            props: {
                icon: <Dollar />
            }
        }, {
            name: 'Модерация',
            to: '/mods',
            requireAuth: true,
            permissions: 'admin',
            props: {
                icon: <Crown />
            }
        }, {
            name: 'База данных тарифов',
            to: '/database/tarifs',
            requireAuth: true,
            permissions: 'admin',
            props: {
                icon: <Database />
            }
        }, {
            name: 'База данных выходных',
            to: '/database/holidays',
            requireAuth: true,
            permissions: 'admin',
            props: {
                icon: <Database />
            }
        }, {
            name: 'База данных дневных зон',
            to: '/database/dayzones',
            requireAuth: true,
            permissions: 'admin',
            props: {
                icon: <Database />
            }
        }, {
            name: 'База данных отчетных часов',
            to: '/database/acc-hours',
            requireAuth: true,
            permissions: 'admin',
            props: {
                icon: <Database />
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
            <ChatWidget/>
            <ScrollRestoration />
            <Routes>
                {/* <Route path="/" element={<HomePage />} /> */}
                <Route path="/" element={<TariffPage />} />

                <Route element={<PrivateRoute/>}>
                    <Route path="/mods" element={<ModeratorsManagementPage/>}/>
                    <Route path="/database">
                        <Route path="tarifs" element={<PowerSupplyDataPage/>}/>
                        <Route path="holidays" element={<HolidaysDatabasePage/>}/>
                        <Route path="dayzones" element={<DayZonesDatabasePage/>}/>
                        <Route path="acc-hours" element={<AccountingHoursDatabasePage/>}/>
                    </Route>
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