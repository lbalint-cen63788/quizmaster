export const LoadedIndicator = ({ isLoaded }: { isLoaded: boolean }) => (
    <input id="is-loaded" type="hidden" value={isLoaded ? 'loaded' : ''} />
)
