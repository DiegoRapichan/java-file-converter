import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FileConverterComponent } from './components/file-converter/file-converter.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, FileConverterComponent],
  template: `
    <app-file-converter></app-file-converter>
    <router-outlet></router-outlet>
  `,
  styles: [`
    :host {
      display: block;
      min-height: 100vh;
      background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    }
  `]
})
export class AppComponent {
  title = 'File Converter';
}
