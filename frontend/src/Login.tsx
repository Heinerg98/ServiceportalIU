import React, { useState } from 'react'
import axios from 'axios'

/**
 * Login-Komponente - WCAG 2.2 AA konform
 *
 * Beschreibung (Deutsch):
 * - Ein einfaches Formular für Benutzername und Passwort.
 * - Beim erfolgreichen Login wird der Basic-Auth-Token in localStorage gespeichert
 *   und als Default-Header in axios eingetragen, damit nachfolgende Anfragen authentifiziert werden.
 * - Beim Logout wird der Header entfernt und localStorage geleert.
 * - Fehlerbehandlung mit ARIA-Live-Region statt alert()
 *
 * Hinweis: Diese einfache Basic-Auth-Variante dient nur Entwicklungszwecken. In Produktion
 * sollte ein sicheres Authentifizierungsverfahren (z. B. HttpOnly-Cookies, JWT mit Refresh) verwendet werden.
 */
export default function Login({ onAuthChange }:{ onAuthChange:(v:boolean)=>void }){
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [loggedIn, setLoggedIn] = useState(!!localStorage.getItem('auth'))
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function login(e?:React.FormEvent){
    if (e) e.preventDefault()
    if (!username || !password) {
      setError('Benutzername und Passwort sind erforderlich')
      return
    }
    
    setLoading(true)
    setError('')
    
    try{
      const token = btoa(`${username}:${password}`)
      axios.defaults.headers.common['Authorization'] = `Basic ${token}`
      await axios.get('/api/me')
      localStorage.setItem('auth', token)
      setLoggedIn(true)
      setUsername('')
      setPassword('')
      onAuthChange(true)
    }catch(err){
      delete axios.defaults.headers.common['Authorization']
      localStorage.removeItem('auth')
      setLoggedIn(false)
      setError('Login fehlgeschlagen. Bitte Benutzername und Passwort überprüfen.')
      onAuthChange(false)
    } finally {
      setLoading(false)
    }
  }

  function logout(){
    delete axios.defaults.headers.common['Authorization']
    localStorage.removeItem('auth')
    setLoggedIn(false)
    setError('')
    setUsername('')
    setPassword('')
    onAuthChange(false)
  }

  if (loggedIn) return (
    <div>
      <button type="button" onClick={logout}>Abmelden</button>
    </div>
  )

  return (
    <form onSubmit={login} style={{display:'inline-flex', gap: '0.5rem', alignItems:'center', flexWrap: 'wrap'}}>
      {/* Error-Message mit ARIA-Live für Barrierefreiheit */}
      {error && (
        <div 
          role="alert" 
          aria-live="assertive" 
          className="error-message"
          style={{width: '100%', marginBottom: '0.5rem'}}
        >
          {error}
        </div>
      )}
      
      <label htmlFor="login-username">Benutzername</label>
      <input 
        id="login-username"
        aria-label="Benutzername" 
        placeholder="Benutzer" 
        value={username} 
        onChange={e=>setUsername(e.target.value)}
        required
        disabled={loading}
      />
      
      <label htmlFor="login-password">Passwort</label>
      <input 
        id="login-password"
        aria-label="Passwort" 
        placeholder="Passwort" 
        type="password" 
        value={password} 
        onChange={e=>setPassword(e.target.value)}
        required
        disabled={loading}
      />
      
      <button type="submit" disabled={loading}>
        {loading ? 'Anmelden...' : 'Anmelden'}
      </button>
    </form>
  )
}
