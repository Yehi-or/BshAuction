import axios from 'axios';
import Vue, { ref } from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate';

Vue.use(Vuex);

const store = new Vuex.Store({
  state: {
    accessToken: null,
    refreshToken: null,
    userNick: null,
    searchRankingList: [],
    userIdNum: null,
    isLoggined : false,
    newAccessTokenState : 0,
  },
  getters: {
    getAccessToken(state) {
      return state.accessToken;
    },
    getRefreshToken(state) {
      return state.accessToken;
    },
    getUserNick(state) {
      return state.userNick;
    },
    getSearchRankingList(state) {
      return state.searchRankingList;
    },
    getUserIdNum(state) {
      return state.userIdNum;
    },
    getIsLoggined(state) {
      return state.isLoggined;
    },
    getNewAccessTokenState(state) {
      return state.newAccessTokenState;
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
    },
    setSearchRankingList(state, searchRankingList) {
      state.searchRankingList = searchRankingList;
    },
    setUserIdNum(state, userIdNum) {
      state.userIdNum = userIdNum;
    },
    setIsLoggined(state, isLoggined) {
      state.isLoggined = isLoggined;
    },
    setNewAccessTokenState(state, newAccessTokenState) {
      state.newAccessTokenState = newAccessTokenState;
    }
  },
  actions: {
    async sendBidMessage({}, data) {
      await data.stomp.send(data.url, data.data);
    },

    async getNewAccessToken({commit}, refreshToken) {
      try {
        const result = await axios.post('/api/token/refreshToken', refreshToken);
        commit('setAccessToken', result.data);
        commit('setNewAccessTokenState', 200);
      } catch(err) {
        if(err.response.state === 401) {
          commit('setNewAccessTokenState', 401);
        } else if(err.response.state === 403) {
          commit('setNewAccessTokenState', 403);
        }
      }
    }
  },
  plugins: [
    createPersistedState({
      paths: ['accessToken', 'refreshToken', 'userNick'],
    })
  ]
})

export default store;