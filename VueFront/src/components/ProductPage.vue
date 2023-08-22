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
          <p>경매 종료까지 남은 시간: {{ formattedTime }}</p>
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
                <input type="checkbox" v-if="isFirstClick && checkMine(data.bidUserName, data.userId)"
                  v-model="selectedBids" :value="data" />
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div id="result_message" ref="result_message"></div>
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
      isFirstClick: false,
      selectedBids: [],
      bidReturnMessage: null,
      sendData: null,
      isBidTry: false,
      isBidCancel: false,
      currentTime: null,
      bidEndTime: null,
      interval: null,
      timeRemain: null,
      seconds: 0,
      minutes: 0,
    }
  },

  async created() {
    this.productId = this.$route.params.id;
    const detailProductList = await axios.get('/api/product/' + this.productId);
    const timeResult = await axios.get('/api/product/bidEndTime/' + this.productId);

    this.currentTime = timeResult.data.currentTime;
    this.bidEndTime = timeResult.data.bidEndTime;

    const endTime = new Date(this.bidEndTime).getTime();
    const now = new Date(this.currentTime).getTime();
    this.timeRemain =  Math.max(endTime - now, 0);

    this.productBidList = detailProductList.data.bidList;
    this.productName = detailProductList.data.productName;
    this.productCurrentPrice = detailProductList.data.price;

  },
  computed : {
    
    timeRemaining() {
      const endTime = new Date(this.bidEndTime).getTime();
      const now = new Date(this.currentTime).getTime();
      return Math.max(endTime - now, 0);
    },

    formattedTime() {
      this.seconds = Math.floor(this.timeRemain / 1000);
      this.minutes = Math.floor(this.seconds / 60);
      const hours = Math.floor(this.minutes / 60);
      const days = Math.floor(hours / 24);

      const remainingHours = hours % 24;
      const remainingMinutes = this.minutes % 60;
      const remainingSeconds = this.seconds % 60;

      return `${days}일 ${remainingHours}시간 ${remainingMinutes}분 ${remainingSeconds}초`;
    },
  },

  mounted() {
    this.updateTimer();

    let sockJs = new SockJS('http://localhost:8100/ws');
    this.stomp = Stomp.over(sockJs);

    this.stomp.connect({}, () => {
      this.stomp.subscribe('/topic/productId.' + this.productId, (chat) => {
        try {
          let content = JSON.parse(chat.body);
          console.log(content);
          //경매 try 또는 경매 취소
          const returnBidTryValue = content.returnBidAttemptDTO;
          const returnBidCancleList = content.bidCancelInfoDTOList;

          //메세지 엘리먼트
          const resultElement = this.$refs.result_message;

          //입찰 작업일 때
          if (returnBidTryValue != null) {
            //작업 메시지
            const returnMessage = returnBidTryValue.returnMessage; 

            const returnUserNick = returnBidTryValue.userNick;
            const returnUserId = returnBidTryValue.userId;
            const tryPrice = content.tryPrice;

            if (returnMessage === 'expired') {
              const refreshToken = this.$store.getters.getRefreshToken;

              this.$store.dispatch('getNewAccessToken', refreshToken);
              const tokenState = this.$store.getters.getNewAccessTokenState;

              if (tokenState == 200) {
                this.$store.dispatch('sendBidMessage', this.sendData);
              } else if (tokenState == 401) {
                //로그아웃
              } else if (tokenState == 403) {
                //다른 작동하게
              }
            }

            const userId = content.userId;
            
            if(userId == sessionStorage.getItem('userIdNum')) {
                if (returnMessage === 'notMatchROLE') {
                this.bidReturnMessage = '권한이 올바르지 않습니다.';
                resultElement.textContent = this.bidReturnMessage;
              } else if (returnMessage === 'requireLogin') {
                alert('로그인이 필요합니다.');
                this.$router.push('/sign');
              } else if (returnMessage === 'accessFail') {
                resultElement.textContent = 'accessFail';
              } else if (returnMessage === 'duplicated') {
                resultElement.textContent = 'duplicated';
              }

            }

            //성공작업일때
            if (returnMessage === 'success') {
              
              if(userId == sessionStorage.getItem('userIdNum')) {
                resultElement.textContent = 'success';
              }

              //10분 미만 입찰 시도 성공했을 때
              if(this.timeRemain < 600000) {
                //알림 요청 axios
                axios.post('/api/bid/bidExtension/' + this.productId);
              }

              const data = {
                bidPrice: tryPrice,
                bidUserName: returnUserNick,
                userId: returnUserId,
              }

              this.productBidList.push(data);
              this.productCurrentPrice = tryPrice;
            }
          }

          //입찰 취소 작업일 때
          else if (returnBidCancleList != null) {
            //작업 메시지
            const returnMessage = content.returnMessage;
            
            if(returnMessage === 'bidCancel' && returnBidCancleList.length > 0) {
             
              for(let i=0; i<returnBidCancleList.length; i++) {
                let deleteObject = returnBidCancleList[i];
                
                const index = this.productBidList.findIndex(item => item.userId == deleteObject.userId && item.bidPrice == deleteObject.bidPrice && item.bidUserName == deleteObject.bidUserName);

                if(index) {
                  this.productBidList.splice(index, 1);
                }
              }
            }
          }
        } catch (err) {
          console.log(err);
        }

      })
    })

    const btn = document.querySelector('#button-send');

    btn.addEventListener('click', (e) => {

      const inputElement = document.getElementById('text');
      const value = inputElement.value;

      const userIdNum = sessionStorage.getItem('userIdNum');
      const url = '/pub/product.bid.' + this.productId;

      if (sessionStorage.getItem("isLoggined") && userIdNum) {

        this.sendData = {
          data: JSON.stringify({
            userId: userIdNum,
            bidPrice: value,
            bidMessageAccessToken: this.$store.getters.getAccessToken,
          }),
          url: url,
          stomp: this.stomp,
        };

        this.$store.dispatch('sendBidMessage', this.sendData);
      } else {
        alert('로그인 후 이용해 주세요');
      }
    })
  },

  methods: {
    bidCancel() {
      const userIdNum = sessionStorage.getItem('userIdNum');

      if (userIdNum !== null) {
        if (!this.isFirstClick) {
          this.isFirstClick = true;
        } else {
          if (this.selectedBids.length == 0) {
            this.isFirstClick = false;
          } else {
            const url = '/pub/product.bidCancel.' + this.productId;

            const data = {
              data: JSON.stringify({
                selectedBids: this.selectedBids,
                bidMessageAccessToken: this.$store.getters.getAccessToken,
              }),
              url: url,
              stomp: this.stomp,
            };

            this.$store.dispatch('sendBidMessage', data);
          }
        }
      } else {
        alert('로그인 후 이용해 주세요');
      }
    },
    updateTimer() {
      this.interval = setInterval(() => {
        this.timeRemain -= 1000;

        if (this.timeRemain === 0) {
          clearInterval(this.interval);
          axios.post('/api/bid/bidEnd');
          return;
        }

      }, 1000); // 1초마다 업데이트
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

.try_bid {
  margin-top: 20px;
}

#one {
  display: flex;
}
</style>