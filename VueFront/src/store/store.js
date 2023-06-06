import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate';

Vue.use(Vuex);

const store = new Vuex.Store({
  state: {
    accessToken: null,
    refreshToken: null,
    userNick: null,
  },
  getters: {
    getAccessToken(state) {
      return state.token;
    },
    getRefreshToken(state) {
      return state.accessToken;
    },
    getUserNick(state) {
      return state.userNick;
    }
  },
  mutations: {
    setAccessToken(state, accessToken) {
        state.accessToken = accessToken;
    },
    setRefreshToken(state, refreshToken) {
      state.refreshToken = refreshToken;
    },
    setUserNick(state, userNick) {
        state.userNick = userNick;
    }
  },
  actions: {
    
  },
  plugins: [
    createPersistedState({
      paths: ['accessToken', 'refreshToken', 'userNick'],
    })
  ]
})

export default store;