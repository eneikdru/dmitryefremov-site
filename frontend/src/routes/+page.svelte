<script lang="ts">
  let isDarkMode = $state(true);
  let showImprint = $state(false);

  function toggleTheme() {
    isDarkMode = !isDarkMode;
  }

  function toggleImprint() {
    showImprint = !showImprint;
  }
</script>

<svelte:head>
  <title>Dmitry Efremov</title>
</svelte:head>

<div class={isDarkMode ? 'dark bg-dark-void text-white' : 'bg-white text-black'}>
  <div class="min-h-screen flex flex-col font-sans transition-colors duration-200">

    <header class="w-full px-4 py-6 flex justify-end">
      <button
        onclick={toggleTheme}
        class="px-4 py-2 text-sm border {isDarkMode ? 'border-dark-border text-gray-300 hover:text-white' : 'border-gray-300 text-gray-700 hover:text-black'} transition-colors"
        aria-label="Toggle theme"
      >
        Toggle theme
      </button>
    </header>

    <main class="flex-grow flex flex-col items-center justify-center w-full px-4 text-center">
      <h1 class="text-5xl md:text-7xl font-bold tracking-tight mb-6">
        Дмитрий Ефремов
      </h1>
      <h2 class="text-xl md:text-2xl mb-8 opacity-80 max-w-2xl">
        Системы, логика мышления и автономные процессы
      </h2>
      <p class="text-lg md:text-xl max-w-3xl italic opacity-70 mb-12">
        "Ваш код и ваши решения — это то, кто вы есть на самом деле."
      </p>

      <div class="flex flex-wrap justify-center gap-6 text-sm mt-8 opacity-90">
        <a href="https://t.me/efremov_mvp" target="_blank" rel="noopener noreferrer" class="hover:underline">Telegram</a>
        <a href="https://youtube.com/@efremov_dmitriy" target="_blank" rel="noopener noreferrer" class="hover:underline">YouTube</a>
        <a href="https://github.com" target="_blank" rel="noopener noreferrer" class="hover:underline">GitHub</a>
        <a href="https://linkedin.com" target="_blank" rel="noopener noreferrer" class="hover:underline">LinkedIn</a>
        <a href="mailto:contact@dmitryefremov.com" class="hover:underline">Email</a>
      </div>
    </main>

    <footer class="w-full px-4 py-8 mt-auto border-t {isDarkMode ? 'border-dark-border' : 'border-gray-200'} flex flex-col items-center">
      <button
        onclick={toggleImprint}
        class="text-sm opacity-60 hover:opacity-100 transition-opacity mb-4"
      >
        Imprint
      </button>

      {#if showImprint}
        <div class="text-xs opacity-60 max-w-2xl text-center pb-4 transition-all duration-300">
          <p>© {new Date().getFullYear()} Dmitry Efremov.</p>
          <p>This is a personal representative site. All materials are provided as-is without any warranties.</p>
        </div>
      {/if}
    </footer>

  </div>
</div>
