<template>
  <div class="home">
    <button @click="goSingPage">로그인</button>
    <button @click="test22">테스트</button>
  </div>
</template>

<script>
import axios from 'axios';
import { access } from 'fs';

export default {
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
    test22() {
      axios.post('/api/bid/test22', null, {
        headers: {
          Authorization: `Bearer ${"eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9VU0VSIiwidXNlcklkIjoxMiwiZXhwIjoxNjg2MDM3Mjk3fQ.BrdfHCQGyxJwJaLUmZfVyIQCdTkFw7h_rpPIBvbERH8"}`,
        }
      })
        .then(response => {
          console.log(response);
        })
        .catch(error => {
          console.error(error);
        });
    },

    async request(data, url) {
      let accessToken = this.$store.getters.getAccessToken;
      let refreshToken = this.$store.getters.getRefreshToken;

      if (!accessToken) {
        if (!refreshToken) {
          this.$router.push('/sign');
          return;
        }
      };

      try {
        const response = await axios.post(url, data, {
          headers: {
            Authorization: `Bearer ${accessToken}`
          }
        });
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
  color: #c4c3ca;
  background-color: #1f2029;
  overflow-x: hidden;
}

.btn {
  border-radius: 4px;
  height: 44px;
  font-size: 13px;
  font-weight: 600;
  text-transform: uppercase;
  -webkit-transition: all 200ms linear;
  transition: all 200ms linear;
  padding: 0 30px;
  letter-spacing: 1px;
  display: -webkit-inline-flex;
  display: -ms-inline-flexbox;
  display: inline-flex;
  -webkit-align-items: center;
  -moz-align-items: center;
  -ms-align-items: center;
  align-items: center;
  -webkit-justify-content: center;
  -moz-justify-content: center;
  -ms-justify-content: center;
  justify-content: center;
  -ms-flex-pack: center;
  text-align: center;
  border: none;
  background-color: #ffeba7;
  color: #102770;
  box-shadow: 0 8px 24px 0 rgba(255, 235, 167, .2);
}

.btn:active,
.btn:focus {
  background-color: #102770;
  color: #ffeba7;
  box-shadow: 0 8px 24px 0 rgba(16, 39, 112, .2);
}

.btn:hover {
  background-color: #102770;
  color: #ffeba7;
  box-shadow: 0 8px 24px 0 rgba(16, 39, 112, .2);
}

a {
  cursor: pointer;
  transition: all 200ms linear;
}

a:hover {
  text-decoration: none;
}

.link {
  color: #c4c3ca;
}

.link:hover {
  color: #ffeba7;
}

p {
  font-weight: 500;
  font-size: 14px;
  line-height: 1.7;
}

h4 {
  font-weight: 600;
}

h6 span {
  padding: 0 20px;
  text-transform: uppercase;
  font-weight: 700;
}</style>