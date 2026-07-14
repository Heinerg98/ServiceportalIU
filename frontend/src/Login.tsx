import React, { useState } from 'react'
import axios from 'axios'

/**
 * Login-Komponente
 *
 * Beschreibung (Deutsch):
 * - Ein einfaches Formular für Benutzername und Passwort.
 * - Beim erfolgreichen Login wird der Basic-Auth-Token in localStorage gespeichert
 *   und als Default-Header in axios eingetragen, damit nachfolgende Anfragen authentifiziert werden.
 * - Beim Logout wird der Header entfernt und localStorage geleert.
 *
 * Hinweis: Diese einfache Basic-Auth-Variante dient nur Entwicklungszwecken. In Produktion
 * sollte ein sicheres Authentifizierungsverfahren (z. B. HttpOnly-Cookies, JWT mit Refresh) verwendet werden.
 */
export default function Login({ onAuthChange }:{ onAuthChange:(v:boolean)=>void }){
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [loggedIn, setLoggedIn] = useState(!!localStorage.getItem('auth'))

  async function login(e?:React.FormEvent){
    if (e) e.preventDefault()
    try{
      const token = btoa(`${username}:${password}`)
      // set header for test call
      axios.defaults.headers.common['Authorization'] = `Basic ${token}`
      // test by calling a safe endpoint that returns the authenticated user (works for any valid user)
      await axios.get('/api/me')
      localStorage.setItem('auth', token)
      setLoggedIn(true)
      onAuthChange(true)
    }catch(err){
      // login failed; clear header
      delete axios.defaults.headers.common['Authorization']
      localStorage.removeItem('auth')
      setLoggedIn(false)
      onAuthChange(false)
      alert('Login fehlgeschlagen (Benutzername/Passwort prüfen)')
    }
  }

  function logout(){
    delete axios.defaults.headers.common['Authorization']
    localStorage.removeItem('auth')
    setLoggedIn(false)
    onAuthChange(false)
  }

  if (loggedIn) return <div>
    <button onClick={logout}>Abmelden</button>
  </div>

  return (
    <form onSubmit={login} style={{display:'inline-flex', gap: '0.5rem', alignItems:'center'}}>
      {/* Visuelle Labels werden versteckt, sind aber für Screenreader vorhanden */}
      <label style={{display:'none'}}>Benutzername<input value={username} onChange={e=>setUsername(e.target.value)} /></label>
      <label style={{display:'none'}}>Passwort<input type="password" value={password} onChange={e=>setPassword(e.target.value)} /></label>
      <input aria-label="Benutzername" placeholder="Benutzer" value={username} onChange={e=>setUsername(e.target.value)} />
      <input aria-label="Passwort" placeholder="Passwort" type="password" value={password} onChange={e=>setPassword(e.target.value)} />
      <button type="submit">Anmelden</button>
    </form>
  )
}
