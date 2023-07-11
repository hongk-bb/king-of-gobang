<template>
  <div class="container">
    <div class="card">
      <div class="card-body">
        <slot>
          <!-- 存放每个页面需要渲染的东西 -->
        </slot>
      </div>
    </div>
  </div>
</template>

<script>

</script>

<style scoped>
div.container {
  margin: 5vh auto;
}
</style>