import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

var store = new Vuex.Store({
  state: {
    clerkInfoAll:{},
    clerkInfo:{},
    authInfo:[],
  },
  mutations: {
    updateInfo(state,info) {
      state.clerkInfoAll = info;
      state.authInfo = info.authModel.map(c => c.id);
      delete info.authModel;
      state.clerkInfo = info;
    },
    clear(state) {
      state.clerkInfoAll = {};
      state.clerkInfo = {};
      state.authInfo = [];
    }
  },
  getters: {
    getInfo(state) {
      return state.clerkInfo;
    },

    getAuth(state) {
      return state.authInfo;
    }
  }
})

export default store
