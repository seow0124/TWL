import Vue from 'vue';
import VueRouter from 'vue-router';
import Home from "../views/homeNav/Home";
import Login from '../views/authentication/Login';
import Signup from '../views/authentication/Signup';
import ChangePwd from '../views/authentication/ChangePwd';
import ChangePwdLogin from '../views/authentication/ChangePwdLogin';
import SelectSkills from '../views/profile/SelectSkills';
import ArticleCreate from '../views/article/ArticleCreate';
import ArticleDetail from '../views/article/ArticleDetail';
import ArticleUpdate from '../views/article/ArticleUpdate';
import Profile from "../views/profile/Profile";
import ArticleSearch from '../views/homeNav/ArticleSearch';
import SocialSignup from '../views/authentication/SocialSignup';
import Logout from '../views/authentication/Logout';
import ArticleCardList from "../components/homeNav/ArticleCardList";
import FollowArticleCardList from "../components/homeNav/FollowArticleCardList";
import PinArticleCardList from "../components/homeNav/PinArticleCardList";
import FindPwd from '../views/authentication/FindPwd.vue';
import Dummy from "../views/Dummy";
import ArticleRecommend from '../views/homeNav/ArticleRecommend';
import CalendarCardList from '../components/calendar/CalendarCardList.vue';
import NotFound from '../views/NotFound';
import store from '@/store/index';
import PrivacyPolicy from '../views/policies/PrivacyPolicy.vue';
import TermsOfUse from '../views/policies/TermsOfUse.vue';
import LoginLoading from "../views/authentication/LoginLoading";

Vue.use(VueRouter);

const routes = [
  {
    path: '/article',
    name: 'Home',
    component: Home,
    children: [
      {
        path: '/latest',
        name: 'ArticleCardList',
        component: ArticleCardList,
      },
      {
        path: '/follow',
        name: 'myfollowArticles',
        component: FollowArticleCardList,
        beforeEnter: requireLogin,
      },
      {
        path: '/pin',
        name: 'mypinArticles',
        component: PinArticleCardList,
        beforeEnter: requireLogin,
      },
    ],
  },
  {
    path: '/',
    name: 'ArticleRecommend',
    component: ArticleRecommend,
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    props: true,
    beforeEnter: alreadyLogin,
  },
  {
    path: '/signup',
    name: 'Signup',
    component: Signup,
    beforeEnter: alreadyLogin,
  },
  {
    path: '/findpwd',
    name: 'FindPwd',
    component: FindPwd,
  },
  {
    path: '/changepwd',
    name: 'ChangePwd',
    component: ChangePwd,
    props: true,
  },
  {
    path: '/changePwdLogin',
    name: 'ChangePwdLogin',
    component: ChangePwdLogin,
    beforeEnter: !isGoogleUser,
  },
  {
    path: '/selectskills',
    name: 'SelectSkills',
    component: SelectSkills,
  },
  {
    path: '/create',
    name: 'ArticleCreate',
    component: ArticleCreate,
    beforeEnter: requireLogin,
  },
  {
    path: '/nowloading',
    name: "LoginLoading",
    component: LoginLoading,
    props: true
  },
  {
    path: '/:id',
    name: 'ArticleDetail',
    component: ArticleDetail,
  },
  {
    path: '/:id/update',
    name: 'ArticleUpdate',
    component: ArticleUpdate,
    props: true,
    beforeEnter: requireLogin,
  },
  {
    path: '/account/:nickname',
    name: 'Profile',
    component: Profile,
  },
  {
    path: '/article/search',
    name: 'ArticleSearch',
    component: ArticleSearch,
    children: [
      {
        path: ':q/:category',
        name: 'ArticleSearchByStack',
        component: ArticleSearch,
      },
    ],
  },
  {
    path: '/social/signup',
    name: 'SocialSignup',
    component: SocialSignup,
    props: true,
  },
  {
    path: '/account/logout',
    name: 'Logout',
    component: Logout,
  },
  {
    path: '/dummy/dummy',
    name: 'Dummy',
    component: Dummy,
  },
  {
    path: '/mypage/date',
    name: 'CalendarCardList',
    component: CalendarCardList,
    props: true,
  },
  {
    path: '/error/404/notfound',
    alias: '*',
    name: 'NotFound',
    component: NotFound,
  },
  {
    path: '/PrivacyPolicy',
    name: 'PrivacyPolicy',
    component: PrivacyPolicy,
  },
  {
    path: '/TermsOfUse',
    name: 'TermsOfUse',
    component: TermsOfUse,
  },
];

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
});

function alreadyLogin(to, from, next) {
  store.getters['isLoggedIn'] ? next('/') : next();
}
function requireLogin(to, from, next) {
  store.getters['isLoggedIn'] ? next() : next('/login');
}
function isGoogleUser(to, from, next) {
  store.state.isGoogleUser ? next() : next('/login');
}

export default router;
