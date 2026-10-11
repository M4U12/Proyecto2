import { Component, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common'; 
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth'; 

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterOutlet, RouterLink, CommonModule], 
  styleUrl: './dashboard.css',
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  rolUsuario: string = ''; 

  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit() {
    this.rolUsuario = localStorage.getItem('rol') || ''; 
  }

  cerrarSesion() {
    this.authService.logout();
  }
}