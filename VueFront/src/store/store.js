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
    }
  },
  actions: {
    async sendBidMessage({getters, dispatch}, data) {
      await data.stomp.send(data.url, data.data);
      // await dispatch('getNewAccessToken', getters.getRefreshToken);
      // dispatch('sendBidMessage', data);
    },

    async getNewAccessToken({commit}, refreshToken) {
      try {
        const result = await axios.post('/api/token/refreshToken', refreshToken);
        console.log(result.data);
        commit('setAccessToken', result.data);
      } catch(err) {
        if(err.response.state === 401) {
          console.log('만료된 토큰값 로그아웃 부탁');
        } else if(err.response.state === 403) {
          console.log('잘못된 토큰값');
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