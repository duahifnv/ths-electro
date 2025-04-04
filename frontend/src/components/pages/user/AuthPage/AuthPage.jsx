import { useEffect, useState } from "react";
import { Home, User } from "../../../../react-envelope/components/dummies/Icons";
import { IconHeader } from "../../../../react-envelope/components/pages/base/IconHeader";
import { PageBase } from "../../../../react-envelope/components/pages/base/PageBase/PageBase";
import { AuthCard } from "../../../../react-envelope/components/widgets/user/AuthCard/AuthCard";
import { useAuth } from "../../../../react-envelope/hooks/useAuth";
import css from './AuthPage.module.css';
import toast from "react-hot-toast";
import { useLocation, useNavigate } from "react-router-dom";
import ExButton from "../../../../react-envelope/components/ui/buttons/ExButton/ExButton";

export const AuthPage = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [frame, setFrame] = useState(0);
    const { auth, user, login, register } = useAuth();
    const [hasLoggedIn, setHasLoggedIn] = useState(false);

    const handleLogin = async (data) => {
        try {
            const response = await login({
                login: data.login,
                password: data.password
            }, data.save, true);

            setHasLoggedIn(true);
        } catch (er) {
            console.log(er);
            // toast.error(er.response.data.message);

            if (er.status == 404) {
                toast.error('Пользователь не найден');
            } else {
                toast.error('Ошибка запроса\nПроверьте правильность введенных данных');
            }
        }
    };

    const handleRegister = async (data) => {
        try {
            const rr = await register({
                login: data.login,
                password: data.password
            }, {
                email: data.email,
                firstname: data.firstname,
                lastname: data.lastname,
                middlename: data.middlename
            });


            toast.success('Регистрация прошла успешно!\nВыполните вход...');
            setFrame(0);
        } catch (er) {
            console.log(er);
            // toast.error(er.response.data.message);
            toast.error('Ошибка запроса. Проверьте правильность введенных данных.');
        }
    };

    useEffect(() => {
        if (auth && user && hasLoggedIn) {
            toast.success(`Добро пожаловать, ${user.firstname}!`);

            const from = location.state?.from || '/';
            navigate(from, { replace: true });
        }
    }, [auth, user, hasLoggedIn]);

    return (
        <PageBase header={<IconHeader text={'Личный кабинет'} icon={<User />} />}
            less
            fullSize
            contentClassName={css.body}>
            <div className={`${css.card}`}>
                <AuthCard className={'center-self'}
                    onLogin={handleLogin}
                    onRegister={handleRegister}
                    selectedFrame={frame}
                    onSelect={setFrame} />
                <ExButton onClick={() => navigate('/')} gap="10px" className={`${css.home} r10`} leftIcon={<Home className='icon-m' />}>На главную</ExButton>
            </div>
        </PageBase>
    );
};