import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

function route(path, file, name, children,auth) {
  return {
    exact: true,
    path: path,
    name: name,
    children: children,
    component: () => import('../pages' + file),
    meta: {
      requireAuth: auth
    }

  }
}

export default new Router({
  routes: [
    {
      path:'/',
      redirect:'/login'
    },
    route('/login','/Login','Login',null,false),

    {
      path:'/index',
      component:() => import('../pages/Layout'),
      children: [
        route('/clerk','/Clerk','Clerk',null,true),
        route('/manauth','/Manauth','Manauth',null,true),
        route('/insurance','/Insurance','Insurance',null,true),
        route('/chart','/Chart','Chart',null,true),
        route('/auth','/Auth','Auth',null,true),
        route('/staff','/Staff','Staff',null,true),
        route('/client','/Client','Client',null,true),
        route('/pan','/Pan','Pan',null,true),
        //route('/add','/Add','Add',null,false),
      ],
      meta: {
        requireAuth: true
      }
    },
    route('/find','/Find','Find',null,null),
  ]
})
