<template>
  <div class="sidemenu">
    <div style="position:relative">
      <input type="text" name="ShareUrl" id="ShareUrl" />
    </div>
    <div @click="clickFollow" class="icon">
      <i class="far fa-user" :class="{pressed : isFollowed}"></i>
      <span class="sideCnt" v-if="isFollowed">-</span>
      <span class="sideCnt" v-else>+</span>
    </div>
    <div @click="clickLike" class="icon">
      <i class="far fa-heart" :class="{pressed : isLiked}"></i>
      <span class="sideCnt">{{ sideMenu.cntLikes }}</span>
    </div>
    <div @click="clickPin" class="icon">
      <i class="far fa-bookmark" :class="{pressed : isPinned}"></i>
      <span class="sideCnt">{{ sideMenu.cntPin }}</span>
    </div>
    <!-- <div class="icon">
      <i class="fab fa-creative-commons-share"></i>
    </div>-->
    <div class="icon share" @click="CopyUrlToClipboard">
      <i class="far fa-share-square" />
    </div>
  </div>
</template>

<script>
import { likeArticle, pinArticle, requestFollow } from "../../api";
import { mapState, mapActions } from "vuex";
import Swal from "sweetalert2/dist/sweetalert2.js";
import "sweetalert2/src/sweetalert2.scss";

export default {
  name: "ArticleDetailSideMenu",
  computed: {
    ...mapState(["id_token", "likeList", "pinList", "followList"]),
  },
  props: {
    sideMenu: Object,
    article: Object,
  },
  data() {
    return {
      id: this.$route.params.id,
      isLiked: null,
      isPinned: null,
      isFollowed: null,
    };
  },
  methods: {
    clickLike() {
      const article_id = this.article.articleid;
      const email = this.$store.state.username;
      if (!email) {
        Swal.fire({
          text: "로그인이 필요한 서비스입니다.",
          icon: "error",
          closeOnClickOutside: true,
          showCancelButton: true,
          confirmButtonText:
            "<span style='text-align:center; margin-left:-12px; position:relative; top:-8px;'>OK</span>",
          confirmButtonColor: "#e6837a",
          cancelButtonText:
            "<span style='text-align:center; margin-left:-12px; position:relative; top:-8px;'>NO</span>",
          cancelButtonColor: "#e6837a",
        }).then((result) => {
          if (result.value) {
            this.$router.push({
              name: "Login",
              query: { redirect: `${article_id}` },
            });
          } else {
            Swal.close();
          }
        });
      } else {
        const params = {
          article_id,
        };
        likeArticle(params, this.id_token)
          .then((res) => {
            const result = res.data.data;
            const likeList = this.likeList;
            if (result === "like 취소") {
              this.sideMenu.cntLikes -= 1;
              const newLike = likeList.filter((like) => {
                return (
                  Number(like.articleid) !== Number(this.article.articleid)
                );
              });
              this.isLiked = false;
              this.$store.commit("setLikeList", newLike);
            } else {
              this.sideMenu.cntLikes += 1;
              likeList.push(this.article);
              this.isLiked = true;
              this.$store.commit("setLikeList", likeList);
            }
          })
          .catch((err) => console.log(err));
      }
    },
    clickPin() {
      const article_id = this.article.articleid;
      const email = this.$store.state.username;
      if (!email) {
        Swal.fire({
          text: "로그인이 필요한 서비스입니다.",
          icon: "error",
          closeOnClickOutside: true,
          showCancelButton: true,
          confirmButtonText:
            "<span style='text-align:center; margin-left:-12px; position:relative; top:-8px;'>OK</span>",
          confirmButtonColor: "#e6837a",
          cancelButtonText:
            "<span style='text-align:center; margin-left:-12px; position:relative; top:-8px;'>NO</span>",
          cancelButtonColor: "#e6837a",
        }).then((result) => {
          if (result.value) {
            this.$router.push({
              name: "Login",
              query: { redirect: `${article_id}` },
            });
          } else {
            Swal.close();
          }
        });
      } else {
        const params = {
          article_id,
        };
        pinArticle(params, this.id_token)
          .then((res) => {
            const result = res.data.data;
            const pinList = this.pinList;
            if (result === "pin 취소") {
              this.sideMenu.cntPin -= 1;
              const newPin = pinList.filter((pin) => {
                return Number(pin.articleid) !== Number(this.article.articleid);
              });
              this.isPinned = false;
              this.$store.commit("setPinList", newPin);
            } else {
              this.sideMenu.cntPin += 1;
              pinList.push(this.article);
              this.isPinned = true;
              this.$store.commit("setPinList", pinList);
            }
          })
          .catch((err) => console.log(err));
      }
    },
    clickFollow() {
      const article_id = this.article.articleid;
      const email = this.$store.state.username;
      const follow = this.article.email;
      if (!email) {
        Swal.fire({
          text: "로그인이 필요한 서비스입니다.",
          icon: "error",
          closeOnClickOutside: true,
          showCancelButton: true,
          confirmButtonText:
            "<span style='text-align:center; margin-left:-12px; position:relative; top:-8px;'>OK</span>",
          confirmButtonColor: "#e6837a",
          cancelButtonText:
            "<span style='text-align:center; margin-left:-12px; position:relative; top:-8px;'>NO</span>",
          cancelButtonColor: "#e6837a",
        }).then((result) => {
          if (result.value) {
            this.$router.push({
              name: "Login",
              query: { redirect: `${article_id}` },
            });
          } else {
            Swal.close();
          }
        });
      } else {
        const params = {
          follow,
        };
        requestFollow(params, this.id_token)
          .then((res) => {
            const result = res.data.data;
            const followLists = this.followList.follow;
            if (result === "unfollow") {
              const newFollowList = followLists.filter((followList) => {
                return followList.followemail !== this.article.email;
              });
              this.isFollowed = false;
              this.$store.commit("setFollowListByEmail", newFollowList);
            } else {
              const newFollow = {
                email: email,
                followemail: follow,
              };
              this.isFollowed = true;
              followLists.push(newFollow);
              this.$store.commit("setFollowListByEmail", followLists);
            }
          })
          .catch((err) => console.log(err));
      }
    },
    CopyUrlToClipboard() {
      const obShareUrl = document.getElementById("ShareUrl");
      obShareUrl.value = window.document.location.href; // 현재 URL 을 세팅해 줍니다.
      obShareUrl.select(); // 해당 값이 선택되도록 select() 합니다
      document.execCommand("copy"); // 클립보드에 복사합니다.
      obShareUrl.blur(); // 선택된 것을 다시 선택안된것으로 바꿈니다.
      Swal.fire({
        text: "URL이 클립보드에 복사되었습니다.",
        icon: "success",
        closeOnClickOutside: true,
        confirmButtonText:
          "<span style='text-align:center; margin-left:-12px; position:relative; top:-8px;'>OK</span>",
        confirmButtonColor: "#e6837a",
      });
      obShareUrl.select();
      document.execCommand("copy");
      obShareUrl.setSelectionRange(0, 9999999);
    },
    ...mapActions(["getGoogleUserInfo"]),
  },
  mounted() {
    if (this.id_token) {
      this.getGoogleUserInfo(this.id_token)
        .then(() => {
          this.isLiked = !!this.likeList.filter(
            (like) => Number(like.articleid) === Number(this.id)
          ).length;
          this.isPinned = !!this.pinList.filter(
            (pin) => Number(pin.articleid) === Number(this.id)
          ).length;
          this.isFollowed = !!this.followList.follow.filter(
            (follow) => follow.followemail === this.article.email
          ).length;
        })
        .catch((err) => console.log(err));
    }
  },
};
</script>

<style scoped>
.sidemenu {
  border-radius: 35px;
  background-color: white;
  padding: 10px;
  position: fixed;
  width: 80px;
  right: 91%;
}
.icon {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-radius: 75px;
  border: 1.5px solid black;
  -moz-border-radius: 75px;
  -webkit-border-radius: 75px;
  width: 50px;
  height: 50px;
  text-align: center;
  font-size: 24px;
  padding: 8px 5px 5px 5px;
  margin: 8px;
  cursor: pointer;
}
.share {
  padding: 5px 5px 5px 5px;
}

#ShareUrl {
  overflow: hidden;
  position: absolute;
  opacity: 0;
}
.pressed {
  color: crimson;
}
@media (max-width: 1024px) {
  .sidemenu {
    bottom: 15px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    width: 120px;
    height: 48px;
    right: 10%;
    border: 1px solid black;
  }
  .icon {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    border: none;
    width: 30px;
    height: 30px;
    text-align: center;
    font-size: 10px;
    padding: 0;
    margin: 1px;
    cursor: pointer;
  }
  .sideCnt {
    margin-top: 0;
    font-size: 11px;
  }
}
@media (min-width: 1025px) {
  .sidemenu {
    top: 200px;
    display: flex;
    justify-content: center;
    flex-direction: column;
    align-items: center;
  }
  .sideCnt {
    margin-top: 2px;
    font-size: 10px;
  }
}

.icon {
  width: 40px;
  height: 40px;
  font-size: 17px;
}

/* @media (max-width: 1024px) and (min-width: 415px){
    .sidemenu {
      top: 200px ;
      display: flex;
      justify-content: center;
      flex-direction: column;
      align-items: center;
      right : 89%;
    }
    .sideCnt {
    margin-top: 2px;
    font-size: 10px;
    }

    .icon {
    width: 35px;
    height: 35px;
    font-size: 14px;
  }


  
  } */
</style>