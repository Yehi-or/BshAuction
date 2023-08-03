<template>
  <div id="searchBar">
    <div class="search">
      <input type="text" placeholder="검색어 입력" class="search_input" id="search_text">
      <img src="https://s3.ap-northeast-2.amazonaws.com/cdn.wecode.co.kr/icon/search.png" @click="this.doSearch">
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  methods: {
    doSearch() {
      const inputElement = document.getElementById('search_text');
      const value = inputElement.value;

      if(value != null) {
        axios.get('/api/search/' + value);
      } 
    }
  }
}
</script>

<style>
#searchBar {
  display: flex;
  justify-content: center;
}

.search {
  position: relative;
  margin-top: 20px;
  width: 300px;
}

.search input {
  width: 100%;
  border: 1px solid #bbb;
  box-sizing: border-box;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
}

.search img {
  position: absolute;
  width: 17px;
  top: 10px;
  right: 12px;
  margin: 0;
}
</style>