import { bootstrapApplication } from '@angular/platform-browser';
import { config } from './app/app.config.server';
import {IndexComponent} from './app/components/index/index.component';

const bootstrap = () => bootstrapApplication(IndexComponent, config);

export default bootstrap;
