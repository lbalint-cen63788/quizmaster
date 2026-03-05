import { Link } from 'react-router-dom'
import './link-button.scss'

interface LinkButtonProps {
    readonly id?: string
    readonly className?: string
    readonly to: string
    readonly children: React.ReactNode
}

export const LinkButton = ({ id, className, to, children }: LinkButtonProps) => (
    <Link id={id} className={`link-button${className ? ` ${className}` : ''}`} to={to}>
        {children}
    </Link>
)
