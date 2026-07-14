import React, { useEffect, useState } from 'react'
import axios from 'axios'
import './App.css'
import Login from './Login'
import AdminPanel from './AdminPanel'

// Typdefinition für Angebotsobjekte
type Offer = { id: number, title: string, description: string }

// Hilfsfunktion: initialen Schriftwert aus localStorage sicher parsen
function parseInitialFont(){
  const raw = localStorage.getItem('fontSize')
  if(!raw) return 16
  const parsed = parseInt(raw)
  return Number.isFinite(parsed) ? parsed : 16
}

export default function App(){
  // States: Angebote, Schriftgröße, Kontrast, Authentifizierungsstatus, Admin-Flag
  const [offers, setOffers] = useState<Offer[]>([])
  const [fontSize, setFontSize] = useState<number>(parseInitialFont())
  const [highContrast, setHighContrast] = useState<boolean>(localStorage.getItem('highContrast')==='true')
  const [authenticated, setAuthenticated] = useState<boolean>(!!localStorage.getItem('auth'))
  const [isAdmin, setIsAdmin] = useState<boolean>(false)

  // Beim Mount die Angebote laden
  useEffect(()=>{ fetchOffers() },[])

  // Wenn sich fontSize ändert: sichere Anwendung der CSS-Variable sowie ein Inline-Fallback
  useEffect(()=>{ 
    // Validierung: stelle sicher, dass keine ungültigen Werte (NaN) gesetzt werden
    const safeFont = Number.isFinite(fontSize) ? Math.round(fontSize) : 16
    // CSS-Variable setzen (wird im index.css verwendet)
    document.documentElement.style.setProperty('--root-font-size', safeFont + 'px')
    // zusätzlich das inline style.fontSize als Fallback (robust gegen Überschreibungen)
    document.documentElement.style.fontSize = safeFont + 'px'
    // persistieren in localStorage
    localStorage.setItem('fontSize', String(safeFont)) 
  },[fontSize])

  // Kontrastmodus: als data-attribute auf dem root-element speichern (CSS reagiert darauf)
  useEffect(()=>{ document.documentElement.dataset.contrast = highContrast ? 'high' : 'normal'; localStorage.setItem('highContrast', String(highContrast)) },[highContrast])

  // Wenn Auth-Status sich ändert: prüfen, ob Nutzer Admin-Rechte hat
  useEffect(()=>{ if (authenticated) checkAdmin(); else setIsAdmin(false) },[authenticated])

  // API-Aufruf: alle Angebote laden (public)
  async function fetchOffers(){
    try{ const res = await axios.get('/api/offers'); setOffers(res.data) } catch(e){ console.error(e) }
  }

  // Prüfen, ob der angemeldete Nutzer Admin-Rechte hat (ein einfacher GET /api/users reicht)
  async function checkAdmin(){
    try{
      await axios.get('/api/users')
      setIsAdmin(true)
    }catch(e){ setIsAdmin(false) }
  }

  // Admin-Funktion: neues Angebot erstellen (einfacher prompt-basiert UI-Flow)
  async function createOffer(){
    const title = window.prompt('Titel des Angebots')
    if(!title) return
    const description = window.prompt('Beschreibung') || ''
    try{
      await axios.post('/api/offers', { title, description })
      fetchOffers()
    }catch(e){ alert('Fehler beim Erstellen (evtl. fehlende Rechte)') }
  }

  return (
    <div className="app">
      {/* Skip-link für Tastaturnutzer */}
      <a className="skip-link" href="#main">Zum Inhalt springen</a>
      <header>
        <h1>Serviceportal</h1>
        <div className="access-controls">
          {/* Schriftgrößensteuerung: A- / A+ Buttons und Anzeige */}
          <label>Schriftgröße: 
            <button type="button" onClick={()=>setFontSize(s=>Math.max(12,typeof s==='number'?s-1:16))} aria-label="kleiner">A-</button>
            <span aria-live="polite"> {fontSize}px </span>
            <button type="button" onClick={()=>setFontSize(s=>Math.min(24,typeof s==='number'?s+1:16))} aria-label="größer">A+</button>
          </label>
          {/* Kontrastmodus Toggle */}
          <label>
            <input type="checkbox" checked={highContrast} onChange={e=>setHighContrast(e.target.checked)} />
            Kontrastmodus
          </label>
          {/* Login-Komponente (sorgt für Authentifizierung und ruft onAuthChange) */}
          <div style={{marginLeft: '1rem'}}>
            <Login onAuthChange={(v:boolean)=>setAuthenticated(v)} />
          </div>
        </div>
      </header>

      <main id="main">
        <section aria-labelledby="offers-title">
          <h2 id="offers-title">Angebote</h2>
          {/* Admin-spezifische Controls nur anzeigen, wenn checkAdmin erfolgreich war */}
          {isAdmin && <div>
            <strong>Admin Controls:</strong>
            <button type="button" onClick={createOffer}>Angebot erstellen</button>
            <div style={{marginTop:8}}>
              <AdminPanel />
            </div>
          </div>}

          {/* Angebotsliste rendern */}
          <ul>
            {offers.map(o => (
              <li key={o.id}>
                <h3>{o.title}</h3>
                <p>{o.description}</p>
                <RequestForm offerId={o.id} offerTitle={o.title} />
              </li>
            ))}
          </ul>
        </section>
      </main>

    </div>
  )
}

// Formular-Komponente für Service-Anfragen
function RequestForm({offerId, offerTitle}:{offerId:number, offerTitle:string}){
  const [name,setName]=useState('')
  const [email,setEmail]=useState('')
  const [message,setMessage]=useState('')
  const [sent, setSent]=useState(false)

  // Submit-Handler: POST an /api/requests
  async function submit(e:React.FormEvent){
    e.preventDefault()
    try{
      await axios.post('/api/requests', { requesterName: name, requesterEmail: email, message: `Anfrage zu ${offerTitle}: ${message}` })
      setSent(true)
    }catch(e){ console.error(e); alert('Fehler beim Absenden') }
  }

  if(sent) return <div role="status">Danke, Ihre Anfrage wurde abgeschickt.</div>

  return (
    <form onSubmit={submit} className="request-form">
      <h4>Anfrage zu: {offerTitle}</h4>
      <label>Ihr Name<input required value={name} onChange={e=>setName(e.target.value)} /></label>
      <label>Email<input required type="email" value={email} onChange={e=>setEmail(e.target.value)} /></label>
      <label>Nachricht<textarea required value={message} onChange={e=>setMessage(e.target.value)} /></label>
      <button type="submit">Absenden</button>
    </form>
  )
}
