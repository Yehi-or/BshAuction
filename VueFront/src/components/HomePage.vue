<template>
  <div class="home">
    <div v-for="(data, index) in productList" :key="index" class="productList">
      <div class="productPrice">
        <img src="@/assets/logo.png" class="imgSize" @click="goDetailProductPage(data.productId)">
      </div>
      <div class="product_info">
        <p :id="data.productName" :ref="data.productName"> {{ data.productName }}</p>
        <p :id="data.productPrice" :ref="data.productPrice"> {{ data.productPrice }}</p>
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
      productList : [],
      stomp : null,
    }
  },
  async created() {
    const productList = await axios.get('/api/product/list');
    this.productList = productList.data;

  },

  async mounted() {

    let sockJs = new SockJS('http://localhost:8100/ws');
    this.stomp = Stomp.over(sockJs);

    this.stomp.connect({}, () => {
      this.stomp.subscribe("/amq/queue/main", (data) => {
        let content = JSON.parse(data.body);

        const productId = content.productId;
        const index = this.productList.findIndex(item => item.productId === productId);

        if(index !== null || index !== undefined) {
          this.productList[index].productPrice = content.productPrice;
        }

      })
    })

  },

  beforeUnmounted() {
    this.stomp.disconnect(() => {
      console.log('disconnect');
    })
  },

  beforeRouteLeave(to, from, next) {
    this.stomp.disconnect(() => {
      console.log('disconnect Home');
      next();
    })
  },

  methods: {
    goSingPage() {
      this.$router.push({
        path: '/sign',
      });
    },
    goAuction() {
      let id = sessionStorage.getItem('id');
      if (!id) {
        const result = this.request('test@test.com');
        console.log(result);
      } else {
        console.log('fail');
      }
    },
    submitForm() {

    },

    goDetailProductPage(id) {
      this.$router.push('/product/' + id);
    },

    async request(data, url) {
      let accessToken = this.$store.getters.getAccessToken;
      let refreshToken = this.$store.getters.getRefreshToken;

      if (!accessToken) {
        if (!refreshToken) {
          this.$router.push('/sign');
          return;
        }
      }

      try {
        // const response = await axios.post(url, data, {
        //   headers: {
        //     Authorization: `Bearer ${accessToken}`
        //   }
        // });
        //정상 데이터 처리 response
      } catch (error) {
        if (error.response && error.response.status === 401) {
          this.$store.commit('setAccessToken', null);
          try {
            const refreshResponse = await axios.post('/api/token', { refreshToken });
            if(refreshResponse) {
              this.$store.commit('setAccessToken', refreshResponse.data.accessToken);
              await this.request(data, url);
            }
          } catch(error) {
            // refresh 토큰도 만료되었을 때 로그아웃 처리하고 새로 로그인하게
            // this.logout();
          }
        } else {
          // 다른 오류 처리
          // 예: this.handleError(error);
        }
      }
    },
  }
}
</script>

<style>
@import url('https://fonts.googleapis.com/css?family=Poppins:400,500,600,700,800,900');

body {
  font-family: 'Poppins', sans-serif;
  font-weight: 300;
  font-size: 15px;
  line-height: 1.7;
  /* color: #c4c3ca; */
  display: flex;
  justify-content: center;
}

a {text-decoration:none;}

.imgSize {
  width: 100%;
  height: 250px;
}

.productList {
  width: 250px;
  height: 280px;
  margin: 15px;
}

.product_info {
  text-align: center;
}

.home {
  display: flex;
}
</style>