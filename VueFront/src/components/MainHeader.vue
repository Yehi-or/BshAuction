<template>
    <header>
        <div class="container">
            <h1 id="logo"><a href="/" class="logo">BSH Auction</a></h1>
            <nav v-if="!this.isLogin">
                <ul>
                    <li><router-link to="/sign">signIn</router-link></li>
                    <li><router-link to="/signUp">signUp</router-link></li>
                </ul>
            </nav>
            <nav v-else>
                <ul>
                    <li>
                        <div class="userInfo">
                            <div class="userNick"> {{ this.userNick }} </div>
                        </div>
                    </li>
                    <li><a href="/sign">logout</a></li>
                </ul>
            </nav>
        </div>
    </header>
</template>

<script>

export default {
    data() {
        return {
            isLogin: false,
            userNick: null,
        };
    },
    mounted() {
        let isLogin = sessionStorage.getItem("isLoggined");
        let userNick = sessionStorage.getItem("userNick");

        this.isLogin = isLogin;
        this.userNick = userNick;
    },
}

</script>

<style>
a:link,
a:visited,
a:hover,
a:active {
    color: var(--baseColor);
}

.container {
    width: 100%;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    border-bottom: 2px solid black;
}

header {
    position: relative;
    width: 100%;
}

header nav {
    display: flex;
    position: relative;
}

header img {
    width: 11.875 rem;
    padding: 0.75 rem 0;
}

header nav ul {
    display: flex;
    list-style-type: none;
    align-items: center;
}

header nav ul li {
    width: 100px;
}

header nav ul li a {
    position: relative;
    display: inline-block;
    line-height: 5 rem;
    font-weight: bold;
    font-size: 1.0625rem;
}</style>