<template>
  <div>
    <div>
      <div v-for="(data, index) in productBidList" :key="index">
        <div class="bidList">
          <div>
            {{ data }}
          </div>
        </div>
      </div>
    </div>
    <div>
      <input type="text" id="text">
      <button class="btn btn-outline-secondary" type="button" id="button-send">경매!</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';

export default {
  data() {
    return {
      productId: null,
      stomp: null,
      productBidList: [],
    }
  },
  async created() {
    this.productId = this.$route.params.id;

    const detailProductList = await axios.get('/api/product/' + this.productId);
    this.productBidList = detailProductList.data.bidList;

  },

  mounted() {
    console.log('mounted Product');
    let sockJs = new SockJS('http://localhost:8100/ws');
    this.stomp = Stomp.over(sockJs);

    this.stomp.connect({}, () => {
      this.stomp.subscribe("/sub/product/" + this.productId, (chat) => {
        let content = JSON.parse(chat.body);
        
        const returnMessage = content.returnMessage;
        const tryPrice = content.tryPrice;

        if(returnMessage === 'success') {
          this.productBidList.push(tryPrice);
        }
        
        // const listElement = document.querySelector('.bidList');
        
        // if(listElement) {
        //   const div = document.createElement('div');

        // }

      })
    })

    const btn = document.querySelector('#button-send');

    btn.addEventListener('click', (e) => {
      const inputElement = document.getElementById('text');
      const value = inputElement.value;

      //userId 해당 사용자 아이디로 변경
      this.stomp.send("/pub/product/bid/" + this.productId, JSON.stringify({ userId: 1, bidPrice: value }));
    })
  },

  beforeUnmounted() {
    this.stomp.disconnect(() => {
      console.log('disconnect');
    })
  },

  beforeRouteLeave(to, from, next) {
    this.stomp.disconnect(() => {
      console.log('disconnect Product');
      next();
    })
  },

}

</script>