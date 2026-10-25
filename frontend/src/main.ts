import ModelOpsPlaygroundView from './components/ModelOpsPlaygroundView.svelte';
import { mount } from 'svelte';

const app = mount(ModelOpsPlaygroundView, {
  target: document.getElementById('app')!
});

export default app;
