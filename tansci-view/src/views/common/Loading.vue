<template>
  <div class="loading">
    <div class="dot" :style="{ 'background-color': dotColor }"></div>
    <div class="dot" :style="{ 'background-color': dotColor }"></div>
    <div class="dot" :style="{ 'background-color': dotColor }"></div>
  </div>
</template>

<script>
export default {
  props: {
    dotColor: {
      type: String,
      default: '#000'
    }
  }

}
</script>

<style lang="scss">
.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 3.28rem;
}

.dot {
  width: 0.72rem;
  height: 0.72rem;
  border-radius: 50%;
  background-color: #000;
  margin: 0 5px;
  opacity: 0.3;
  animation: dotWave 1.5s ease-in-out infinite;
}

@keyframes dotWave {

  0%,
  60%,
  100% {
    transform: scale(0.5);
    opacity: 0.3;
  }

  30% {
    transform: scale(1);
    opacity: 1;
  }
}
</style>