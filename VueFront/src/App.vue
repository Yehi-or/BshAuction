<template>
  <div id="app">
    <MainHeader></MainHeader>
    <SearchBar></SearchBar>
    <SearchRanking></SearchRanking>
    <router-view/>
  </div>
</template>

<script>
import axios from 'axios';
import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';

import mainHeader from '@/components/MainHeader.vue';
import searchBar from '@/components/SearchBar.vue';
import searchRanking from '@/components/SearchRanking.vue';

export default {
  components : {
    MainHeader : mainHeader,
    SearchBar : searchBar,
    SearchRanking : searchRanking,
  },
  name: 'App',
  data() {
    return {
      makeSetList : [],
    }
  },
  async created() {
    const rankingList = await axios.get('/api/ranking/searchRanking');
    this.$store.commit('setSearchRankingList', rankingList.data);
  },

  mounted() {
    let sockJs = new SockJS('http://localhost:8100/ws');
    this.stomp = Stomp.over(sockJs);

    this.stomp.connect({}, () => {
      this.stomp.subscribe("/sub/search/ranking/", (data) => {
        let rankingList = JSON.parse(data.body);
        if(rankingList) {
          this.$store.commit('setSearchRankingList', rankingList);
        }
      })
    })
  },

  
}
</script>

<style>
  #app {
    width : 1500px;
  }
  
  body {
    margin: 0;
  }

  * {
    box-sizing: border-box;
  }
</style>
