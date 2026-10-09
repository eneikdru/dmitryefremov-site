import TelemetryHub from './components/TelemetryHub.svelte';
import { mount } from 'svelte';

const app = mount(TelemetryHub, {
  target: document.getElementById('app')!
});

export default app;
