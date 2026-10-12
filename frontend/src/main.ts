import CanonReaderView from './components/CanonReaderView.svelte';
import { mount } from 'svelte';

const app = mount(CanonReaderView, {
  target: document.getElementById('app')!
});

export default app;
