// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import App from './App.vue'
import Vuex from 'vuex'
import qs from 'qs'
import './http'
import store from './store'

Vue.use(ElementUI);
Vue.prototype.$qs = qs

Vue.config.productionTip = false



new Vue({
  el: '#app',
  router: router,
  store: store,
  render: h => h(App)
});


router.beforeEach((to,from,next) =>{
  if (to.meta.requireAuth) {
    if (Object.keys(store.state.clerkInfoAll).length !== 0) {  // 通过vuex state获取当前的token是否存在
      next();
    } else {
      next({
        path: '/login',
      })
    }
  } else {
    next();
  }
})
