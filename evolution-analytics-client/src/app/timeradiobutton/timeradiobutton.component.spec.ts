import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TimeradiobuttonComponent } from './timeradiobutton.component';

describe('TimeradiobuttonComponent', () => {
  let component: TimeradiobuttonComponent;
  let fixture: ComponentFixture<TimeradiobuttonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TimeradiobuttonComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TimeradiobuttonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
