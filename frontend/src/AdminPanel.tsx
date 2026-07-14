import React, { useState } from 'react'
import RequestsAdmin from './RequestsAdmin'
import UsersAdmin from './UsersAdmin'

// AdminPanel: kleines Steuer-UI, das zwischen Requests- und Users-Admin wechselt
export default function AdminPanel(){
  const [tab, setTab] = useState<'requests'|'users'>('requests')

  return (
    <div className="admin-panel" role="region" aria-label="Administration">
      <h2>Administration</h2>
      <nav aria-label="Admin-Navigation">
        {/* Navigation per Button; aria-pressed gibt den aktiven Zustand an */}
        <button onClick={()=>setTab('requests')} aria-pressed={tab==='requests'}>Anfragen</button>
        <button onClick={()=>setTab('users')} aria-pressed={tab==='users'}>Benutzer</button>
      </nav>
      <section style={{marginTop: '1rem'}}>
        {tab==='requests' ? <RequestsAdmin /> : <UsersAdmin />}
      </section>
    </div>
  )
}
