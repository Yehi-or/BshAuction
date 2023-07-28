<template>
  <div class="detail_product_container" id="one">
    <div class="first">
      <div class="img_container">
        <img class="product_img" src="@/assets/logo.png">
      </div>
      <p class="product_name"> {{ this.productName }}</p>
      <p class="product_price"> 현재 가격 : {{ this.productCurrentPrice }}</p>
    </div>
    <div class="second">
      <div id="fifth">
        <div class="emojiright">
          <p class="coffeename"> 상품명 : {{ this.productName }}</p>
        </div>
      </div>
      <div class="table_container">
          <table class="inner_table">
            <thead>
              <tr>
                <th>입찰가</th>
                <th>입찰자</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(data, index) in productBidList" :key="index">
                <td>{{ data.bidPrice }}</td>
                <td>{{ data.bidUserName }}</td>
                <td>
                  <input type="checkbox" v-if="isFirstClick && checkMine(data.bidUserName, data.userId)" v-model="selectedBids" :value="data" />
                </td>
              </tr>
            </tbody>
          </table>
      </div>
      <div id="result_message"></div>
      <div class="try_bid">
        <input type="text" id="text">
        <button class="btn btn-outline-secondary" type="button" id="button-send">입찰</button>
        <button type="button" id="bid_cancle" ref="bid_cancle" @click="bidCancel()">입찰취소</button>
      </div>
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
      productName: null,
      productCurrentPrice: null,
      isFirstClick : false,
      selectedBids : [],
    }
  },
  async created() {

    this.productId = this.$route.params.id;
    const detailProductList = await axios.get('/api/product/' + this.productId);

    this.productBidList = detailProductList.data.bidList;
    this.productName = detailProductList.data.productName;
    this.productCurrentPrice = detailProductList.data.price;

  },

  mounted() {
    
    let sockJs = new SockJS('http://localhost:8100/ws');
    this.stomp = Stomp.over(sockJs);

    this.stomp.connect({}, () => {
      this.stomp.subscribe("/sub/product/" + this.productId, (chat) => {
        try {
          let content = JSON.parse(chat.body);
          
          const returnValue = content.returnBidAttemptDTO;
          const returnMessage = returnValue.returnMessage;
          const returnUserNick = returnValue.userNick;
          const tryPrice = content.tryPrice;

          if (returnMessage === 'notMatchROLE') {

          }

          if (returnMessage === 'success') {

            const data = {
              bidPrice : tryPrice,
              bidUserName : returnUserNick,
            }

            this.productBidList.push(data);
            this.productCurrentPrice = tryPrice;
          }

        } catch (err) {
          console.log(err);
        }

        // const listElement = document.querySelector('.bidList');

        // if(listElement) {
        //   const div = document.createElement('div');

        // }

      })
    })

    const btn = document.querySelector('#button-send');
    const cancleBtn = this.$refs.bid_cancle;

    btn.addEventListener('click', (e) => {
      const inputElement = document.getElementById('text');
      const value = inputElement.value;
      
      const userIdNum = sessionStorage.getItem('userIdNum');
      const url = '/pub/product/bid/' + this.productId;

      if (sessionStorage.getItem("isLoggined") && userIdNum) {
        const data = {
          data: JSON.stringify({
            userId: userIdNum,
            bidPrice: value,
            bidMessageAccessToken: this.$store.getters.getAccessToken,
          }),
          url: url,
          stomp: this.stomp,
        };

        this.$store.dispatch('sendBidMessage', data);
      } else {
        alert('로그인 후 이용해 주세요');
      }
    })
  },

  methods : {
    bidCancel() {
      const userIdNum = sessionStorage.getItem('userIdNum');

      if(userIdNum !== null) {
        if(!this.isFirstClick) {
          this.isFirstClick = true;
        }else {
          if(this.selectedBids.length == 0) {
            this.isFirstClick = false;
          } else {
            console.log(this.selectedBids);
            const url = '/pub/product/bidCancel/' + this.productId;

            const data = {
              data: JSON.stringify({
                selectedBids: this.selectedBids,
              }),
              url: url,
              stomp: this.stomp,
            };

            this.$store.dispatch('sendBidMessage', data);
          }
        }
      }else {
        alert('로그인 후 이용해 주세요');
      }
    },
    checkMine(userName, userId) {
      const userIdNum = sessionStorage.getItem('userIdNum');
      return userIdNum == userId;
    }
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

<style>
.detail_product_container {
  width: 1200px;
  margin: 10px auto;
}

.detail_product_container .product_img {
  width: 250px;
  height: 250px;
}

.detail_product_container .product_name {
  font-size: 20px;
  font-weight: bold;
  text-align: center;
}

.first {
  margin-top: 100px;
  width: 50%;
  margin-right: 5px;
}

.second {
  width: 50%;
}

.img_container {
  justify-content: center;
  display: flex;
}

.table_container {
  width: 100%;
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #ccc
}

.table_container td {
  border: 1px solid #ccc;
  padding: 8px;
  text-align: center;
}

.inner_table {
  width: 100%;
}

.product_price {
  text-align: center;
}

.try_bid{
  margin-top: 20px;
}

#one {
  display: flex;
}
</style>