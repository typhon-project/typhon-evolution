import {async, TestBed} from '@angular/core/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {ChartsComponent} from './charts.component';

describe('ChartsComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      declarations: [
        ChartsComponent
      ],
    }).compileComponents();
  }));

  it('should create the module', () => {
    const fixture = TestBed.createComponent(ChartsComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
