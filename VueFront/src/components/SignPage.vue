<template>
  <div>
    <h6 class="mb-0 pb-3"><span>Log In </span><span>Sign Up</span></h6>
    <div class="card-3d-wrap mx-auto">
      <div class="card-3d-wrapper">
        <div class="card-front">
          <div class="center-wrap">
            <div class="section text-center">
              <h4 class="mb-4 pb-3">Log In</h4>
              <form id="loginForm" @submit.prevent="loginSubmit()" method="post">
                <div class="form-group">
                  <input type="email" name="loginEmail" class="form-style" placeholder="Your Email" id="loginEmail"
                    autocomplete="off" v-model="loginForm.email">
                  <i class="input-icon uil uil-at"></i>
                </div>
                <div class="form-group mt-2">
                  <input type="password" name="loginPasswd" class="form-style" placeholder="Your Password"
                    id="loginPasswd" autocomplete="off" v-model="loginForm.password">
                  <i class="input-icon uil uil-lock-alt"></i>
                </div>
                <input type="hidden">
                <button type="submit" class="btn mt-4">Submit</button>
              </form>
            </div>
          </div>
        </div>
        <div class="card-back">
          <div class="center-wrap">
            <div class="section text-center">
              <h4 class="mb-4 pb-3">Sign Up</h4>
              <form id="signUpForm" @submit.prevent="signUpSubmit()" method="post">
                <div class="form-group">
                  <input type="text" name="signUpname" class="form-style" placeholder="Your Full Name" id="signUpname"
                    autocomplete="off" v-model="signUpForm.nick">
                  <i class="input-icon uil uil-user"></i>
                </div>
                <div class="form-group mt-2">
                  <input type="email" name="signUpemail" class="form-style" placeholder="Your Email" id="signUpemail"
                    autocomplete="off" v-model="signUpForm.email">
                  <i class="input-icon uil uil-at"></i>
                </div>
                <div class="form-group mt-2">
                  <input type="password" name="signUppasswd" class="form-style" placeholder="Your Password"
                    id="signUppasswd" autocomplete="off" v-model="signUpForm.password">
                  <i class="input-icon uil uil-lock-alt"></i>
                </div>
                <div class="form-group mt-2">
                  <input type="password" name="signUppasswdConfirm" class="form-style" placeholder="Your Password Confirm"
                    id="signUppasswdConfirm" autocomplete="off" v-model="signUpForm.passwordConfirm">
                  <i class="input-icon uil uil-lock-alt"></i>
                </div>
                <button type="submit" class="btn mt-4">Submit</button>
              </form>
            </div>
          </div>
        </div>
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
      mode: 'signIn',
      loginForm: {
        email: '',
        password: '',
      },
      signUpForm: {
        nick: '',
        email: '',
        password: '',
        passwordConfirm: '',
      },
      loginMessage: '',
      stomp: null,
    }
  },
  mounted() {
  },
  methods: {
    loginSubmit() {
      const data = {
        userEmail: this.loginForm.email,
        password: this.loginForm.password
      };

      axios.post('/api/user/signIn', data)
        .then(response => {
          console.log(response.data.accessToken);
          this.$store.commit('setAccessToken', response.data.accessToken);
          this.$store.commit('setRefreshToken', response.data.refreshToken);
          sessionStorage.setItem('userNick', response.data.userNick);
          sessionStorage.setItem('userIdNum', response.data.userId);
          sessionStorage.setItem('isLoggined', true);

          let sockJs = new SockJS('http://localhost:8100/ws');
          this.stomp = Stomp.over(sockJs);

          //개별 큐 (알림을 받을 예정)
          this.stomp.connect({}, () => {
            this.stomp.subscribe("/topic/userId." + sessionStorage.getItem('userIdNum'), (data) => {
              let content = JSON.parse(data.body);
              console.log(content);
            })
          })

          this.loginMessage = response.data.loginMessage;
          console.log(this.loginMessage);
          this.$router.push('/');
        })
        .catch(error => {
          console.error(error);
        });
    },
    signUpSubmit() {
      const data = {
        userNick: this.signUpForm.nick,
        userEmail: this.signUpForm.email,
        password: this.signUpForm.password,
      };

      axios.post('/api/user/signUp', data)
        .then(response => {
          const message = response.data;
          console.log(message);
          this.$router.replace('/sign');
        })
        .catch(error => {
          if (error.response) {
            const status = error.response.status;
            const errorMessage = error.message;
            console.error(errorMessage);
            if (status === 419) {
              alert('이미 존재하는 회원입니다. 회원가입을 새로 해주세요');
            }
          }
        });
    }
  }
}
</script>
<!-- <style>

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
h6 span{
  padding: 0 20px;
  text-transform: uppercase;
  font-weight: 700;
}
.section{
  position: relative;
  width: 100%;
  display: block;
}
.full-height{
  min-height: 100vh;
}
[type="checkbox"]:checked,
[type="checkbox"]:not(:checked){
  position: absolute;
  left: -9999px;
}
.checkbox_sign:checked + label,
.checkbox_sign:not(:checked) + label{
  position: relative;
  display: block;
  text-align: center;
  width: 60px;
  height: 16px;
  border-radius: 8px;
  padding: 0;
  margin: 10px auto;
  cursor: pointer;
  background-color: #ffeba7;
}
.checkbox_sign:checked + label:before,
.checkbox_sign:not(:checked) + label:before{
  position: absolute;
  display: block;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  color: #ffeba7;
  background-color: #102770;
  font-family: 'unicons';
  content: '\eb4f';
  z-index: 20;
  top: -10px;
  left: -10px;
  line-height: 36px;
  text-align: center;
  font-size: 24px;
  transition: all 0.5s ease;
}
.checkbox_sign:checked + label:before {
  transform: translateX(44px) rotate(-270deg);
}


.card-3d-wrap {
  position: relative;
  width: 440px;
  max-width: 100%;
  height: 550px;
  -webkit-transform-style: preserve-3d;
  transform-style: preserve-3d;
  perspective: 800px;
  margin: 60px auto; /* 중앙에 정렬 */
}
.card-3d-wrapper {
  width: 100%;
  height: 100%;
  position:absolute;    
  top: 0;
  left: 0;  
  -webkit-transform-style: preserve-3d;
  transform-style: preserve-3d;
  transition: all 600ms ease-out; 
}
.card-front, .card-back {   
  width: 100%;
  height: 100%;
  background-color: #2a2b38;
  background-image: url('https://s3-us-west-2.amazonaws.com/s.cdpn.io/1462889/pat.svg');
  background-position: bottom center;
  background-repeat: no-repeat;
  background-size: 300%;
  position: absolute;
  border-radius: 6px;
  left: 0;
  top: 0;
  -webkit-transform-style: preserve-3d;
  transform-style: preserve-3d;
  -webkit-backface-visibility: hidden;
  -moz-backface-visibility: hidden;
  -o-backface-visibility: hidden;
  backface-visibility: hidden;
}
.card-back {
  transform: rotateY(180deg);
}
.checkbox_sign:checked ~ .card-3d-wrap .card-3d-wrapper {
  transform: rotateY(180deg);
}
.center-wrap{
  position: absolute;
  width: 100%;
  top: 50%;
  left: 0;
  transform: translate3d(0, -50%, 35px) perspective(100px);
  z-index: 20;
  display: block;
}


.form-group{ 
  position: relative;
  display: block;
    margin: 0;
    padding: 0;
}
.form-style {
  padding: 13px 20px;
  padding-left: 55px;
  height: 48px;
  width: 100%;
  font-weight: 500;
  border-radius: 4px;
  font-size: 14px;
  line-height: 22px;
  letter-spacing: 0.5px;
  outline: none;
  color: #c4c3ca;
  background-color: #1f2029;
  border: none;
  -webkit-transition: all 200ms linear;
  transition: all 200ms linear;
  box-shadow: 0 4px 8px 0 rgba(21,21,21,.2);
}
.form-style:focus,
.form-style:active {
  border: none;
  outline: none;
  box-shadow: 0 4px 8px 0 rgba(21,21,21,.2);
}
.input-icon {
  position: absolute;
  top: 0;
  left: 18px;
  height: 48px;
  font-size: 24px;
  line-height: 48px;
  text-align: left;
  color: #ffeba7;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}

.form-group input:-ms-input-placeholder  {
  color: #c4c3ca;
  opacity: 0.7;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}
.form-group input::-moz-placeholder  {
  color: #c4c3ca;
  opacity: 0.7;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}
.form-group input:-moz-placeholder  {
  color: #c4c3ca;
  opacity: 0.7;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}
.form-group input::-webkit-input-placeholder  {
  color: #c4c3ca;
  opacity: 0.7;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}
.form-group input:focus:-ms-input-placeholder  {
  opacity: 0;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}
.form-group input:focus::-moz-placeholder  {
  opacity: 0;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}
.form-group input:focus:-moz-placeholder  {
  opacity: 0;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}
.form-group input:focus::-webkit-input-placeholder  {
  opacity: 0;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}

.btn{  
  border-radius: 4px;
  height: 44px;
  font-size: 13px;
  font-weight: 600;
  text-transform: uppercase;
  -webkit-transition : all 200ms linear;
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
  box-shadow: 0 8px 24px 0 rgba(255,235,167,.2);
}
.btn:active,
.btn:focus{  
  background-color: #102770;
  color: #ffeba7;
  box-shadow: 0 8px 24px 0 rgba(16,39,112,.2);
}
.btn:hover{  
  background-color: #102770;
  color: #ffeba7;
  box-shadow: 0 8px 24px 0 rgba(16,39,112,.2);
}
.logo {
  position: absolute;
  top: 30px;
  right: 30px;
  display: block;
  z-index: 100;
  transition: all 250ms linear;
}
.logo img {
  height: 26px;
  width: auto;
  display: block;
}
</style> -->
