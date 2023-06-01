import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate';

Vue.use(Vuex);

const store = new Vuex.Store({
  state: {
    token: '',
    userNick: '',
  },
  getters: {
    getToken(state) {
        return state.token;
    },
    getUserNick(state) {
        return state.userNick;
    }
  },
  mutations: {
    setToken(state, token) {
        state.token = token;
    },
    setUserNick(state, userNick) {
        state.userNick = userNick;
    }
  },
  actions: {
    
  },
  plugins: [
    createPersistedState({
      paths: ['token', 'userNick'],
    })
  ]
})

export default store;