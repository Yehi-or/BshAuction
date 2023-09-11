<template>
    <div class="searchRanking" ref="searchRanking">
        <div>
            <div class="type_word">
                <p>실시간 인기 검색어</p>
            </div>
            <ul v-for="(data, index) in searchRankingList" :key="index">
                <li>
                    {{ data }}
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
    data() {
        return {
            currentPosition: 0,
            searchElement: null,
        }
    },
    computed: {
        ...mapState(['searchRankingList']),
    },
    mounted() {
        const searchElement = this.$refs.searchRanking;
        this.searchElement = searchElement;
        const top = (getComputedStyle(searchElement).top).replace('px', "");
        this.currentPosition = parseInt(top);

        window.addEventListener('scroll', this.handleScroll);
    },
    methods: {
        handleScroll() {
            let scrollY = window.scrollY;
            requestAnimationFrame(() => {
                this.searchElement.style.top = scrollY + this.currentPosition + 'px';
            });
        }
    }
}
</script>

<style>
.type_word p {
    font-size: x-small;
}

.type_word {
    text-align: center;
}

.searchRanking div,
ul,
li {
    -webkit-box-sizing: border-box;
    -moz-box-sizing: border-box;
    box-sizing: border-box;
    padding: 0;
    margin: 0;
}

.searchRanking {
    position: absolute;
    width: 100px;
    top: 50%;
    margin-top: -50px;
    right: 10px;
    background: #fff;
    transform: translateX(-50%);
    transition: top 0.3s ease;
}

.searchRanking ul {
    position: relative;
    float: left;
    width: 100%;
    /* display: inline-block; */
    display: inline;
    border: 1px solid #ddd;
}

.searchRanking ul li {
    float: left;
    width: 100%;
    border-bottom: 1px solid #ddd;
    text-align: center;
    /* display: inline-block; */
    display: inline;
}

.searchRanking ul li a {
    position: relative;
    float: left;
    width: 100%;
    height: 30px;
    line-height: 30px;
    text-align: center;
    color: #999;
    font-size: 9.5pt;
}

.searchRanking ul li a:hover {
    color: #000;
}

.searchRanking ul li:last-child {
    border-bottom: 0;
}

.content {
    position: relative;
    min-height: 1000px;
}</style>